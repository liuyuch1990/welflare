package com.unicorn.wsp.service.impl;



import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.unicorn.wsp.common.annotation.Excel;
import com.unicorn.wsp.common.exception.BusinessRollbackException;
import com.unicorn.wsp.common.result.Result;
import com.unicorn.wsp.entity.WspCardQuota;
import com.unicorn.wsp.entity.WspCardType;
import com.unicorn.wsp.entity.WspGiftCard;
import com.unicorn.wsp.entity.WspGoods;
import com.unicorn.wsp.entity.exportvo.WspGiftCardETO;
import com.unicorn.wsp.entity.vo.WspGiftCardVO;
import com.unicorn.wsp.entity.zznvo.GiftCardPageVo;
import com.unicorn.wsp.entity.zznvo.GiftCardVo;
import com.unicorn.wsp.mapper.WspCardQuotaMapper;
import com.unicorn.wsp.mapper.WspCardTypeMapper;
import com.unicorn.wsp.mapper.WspGiftCardMapper;
import com.unicorn.wsp.mapper.WspGoodsMapper;
import com.unicorn.wsp.service.WspCardQuotaService;
import com.unicorn.wsp.service.WspGiftCardService;
import com.unicorn.wsp.vo.CardTypeQuota;
import com.unicorn.wsp.vo.PageVo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.nutz.lang.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import org.apache.commons.lang.time.DateUtils;


/**
* <p>
    *  服务类
    * </p>
*
* @author spark
* @since 1.0
*/
@Slf4j
@EnableScheduling
@Service
public class WspGiftCardServiceImpl extends ServiceImpl<WspGiftCardMapper,WspGiftCard>
    implements WspGiftCardService {

    @Autowired
    WspCardQuotaMapper quotaMapper;

    @Autowired
    WspGiftCardMapper cardMapper;

    @Autowired
    WspGoodsMapper wspGoodsMapper;

    @Autowired
    WspCardQuotaService quotaService;

    @Autowired
    WspCardTypeMapper typeMapper;


    // 取消订单后重置礼品卡
    public boolean reSetCard(String cardNum){
        LambdaQueryWrapper<WspGiftCard> wrapper = new LambdaQueryWrapper<>();
        if(StringUtils.isEmpty(cardNum)){
            return false;
        }
        //wrapper.eq(WspGiftCard::getGiftCardNum,cardNum);
        wrapper.eq(WspGiftCard::getGiftCardId,cardNum);
        wrapper.ne(WspGiftCard::getIsDel, "1");// 没删除的
        wrapper.eq(WspGiftCard::getIsUse,"2");// 已使用的
        wrapper.eq(WspGiftCard::getIsTrue,"1");// 有效的

        WspGiftCard card = new WspGiftCard();
        card.setIsUse("1");
        int update = cardMapper.update(card, wrapper);

        if(update > 0){
            return true;
        }
        return false;
    }

    // 临时 不让用户添加多张卡片
    public Integer onlyOneCard(String userId){
        LambdaQueryWrapper<WspGiftCard> wrapper = new LambdaQueryWrapper<>();
        wrapper.ne(WspGiftCard::getIsDel, "1");// 没删除的
        wrapper.eq(WspGiftCard::getUserId, userId);// 该用户的
        wrapper.eq(WspGiftCard::getIsTrue,"1");// 有效
        wrapper.eq(WspGiftCard::getIsUse,"1");// 绑定的
        WspGiftCard card = cardMapper.selectOne(wrapper);
        if(ObjectUtil.isNull(card)){
            return 0;
        }
        return 1;
    }


    // 用户绑定卡号
    public int exchange(String cardNum,String userId,String phone){
        log.info("用户兑换卡号入参 - {} , {}",cardNum,userId);
        LambdaQueryWrapper<WspGiftCard> cardWrapper = new LambdaQueryWrapper<>();
        cardWrapper.eq(WspGiftCard::getGiftCardNum,cardNum);
        cardWrapper.eq(WspGiftCard::getIsUse,"0");
        WspGiftCard card = new WspGiftCard();
        card.setIsUse("1");
        card.setUserId(userId);
        card.setUserPhone(phone);
        log.info("用户兑换，编辑dto - {}",card);
        return cardMapper.update(card, cardWrapper);
    }


    /**
     * 清除礼品卡绑定状态
     * */
    public boolean unbound(int giftCardId){
        LambdaQueryWrapper<WspGiftCard> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WspGiftCard::getGiftCardId,giftCardId);
        wrapper.ne(WspGiftCard::getIsDel, "1");// 没删除的
        wrapper.eq(WspGiftCard::getIsUse,"1");// 已绑定未使用的
        wrapper.eq(WspGiftCard::getIsTrue,"1");// 有效的
        WspGiftCard card = new WspGiftCard();
        card.setIsUse("0");
        card.setUserPhone("");
        card.setUserId("");
        int update = cardMapper.update(card, wrapper);
        if(update>1) {
            log.info("清除礼品卡状态异常-giftCardId=: {}",giftCardId);
            throw new BusinessRollbackException("清除礼品卡状态异常");
        }
        return update == 1;
    }


    /** 绑定用（废除）*/
    // 校验卡号是否存在
    public boolean isExist(String cardNum){
        LambdaQueryWrapper<WspGiftCard> cardWrapper = new LambdaQueryWrapper<>();
        cardWrapper.ne(WspGiftCard::getIsDel,"1");//没删除
        cardWrapper.eq(WspGiftCard::getIsUse,"0");// 未绑定
        cardWrapper.eq(WspGiftCard::getGiftCardNum, cardNum); //相同卡号

        // 不能单单从isTrue判断，有可能长时间没有人调用礼品卡list，导致过期的礼品卡的isTrue属性得不到刷新
        cardWrapper.ne(WspGiftCard::getIsTrue,"0");

        // 是否存在
        List<WspGiftCard> cardList = cardMapper.selectList(cardWrapper);
        if(cardList.size() == 0){
            return false;
        }

        // 过期判断
/*        WspGiftCard card = cardList.get(0);
        String validDate = card.getValidDate();
        String valid = changeValid(card.getGiftCardId(), validDate);
        if("1".equals(valid)){
            return true;
        }else {
            return false;
        }*/
        return true;
    }

    /**
     * 绑定用
     * 迭代：绑定时判断是否存在时，要根据公司编号查询
     * */
    public boolean isExist(String cardNum, String comNum){
        LambdaQueryWrapper<WspGiftCard> cardWrapper = new LambdaQueryWrapper<>();
        cardWrapper.ne(WspGiftCard::getIsDel,"1");//没删除
        cardWrapper.eq(WspGiftCard::getIsUse,"0");// 未绑定
        cardWrapper.eq(WspGiftCard::getGiftCardNum, cardNum); //相同卡号
        /** 没过期*/
        cardWrapper.eq(WspGiftCard::getIsTrue, "1");
        /** 对应公司*/
        cardWrapper.eq(WspGiftCard::getComNum, comNum);

        // 不能单单从isTrue判断，有可能长时间没有人调用礼品卡list，导致过期的礼品卡的isTrue属性得不到刷新
        // 现在能
        cardWrapper.ne(WspGiftCard::getIsTrue,"0");

        // 是否存在
        List<WspGiftCard> cardList = cardMapper.selectList(cardWrapper);
        if(cardList.size() == 0){
            return false;
        }

        // 过期判断(-- 改为用sql根据isTrue判断 --)
        // WspGiftCard card = cardList.get(0);
        // String validDate = card.getValidDate();
        // String valid = changeValid(card.getGiftCardId(), validDate);
        // if("1".equals(valid)){
        //     return true;
        // }else {
        //     return false;
        // }
        return true;
    }


    /**
     * 登录用
     * 迭代：根据公司校验是礼品卡是否存在
     * */
    public boolean isExistLogin(String cardNum, String comNum){
        LambdaQueryWrapper<WspGiftCard> cardWrapper = new LambdaQueryWrapper<>();
        cardWrapper.ne(WspGiftCard::getIsDel,"1");//没删除
        cardWrapper.eq(WspGiftCard::getGiftCardNum, cardNum); //相同卡号
        /** 对应公司*/
        cardWrapper.eq(WspGiftCard::getComNum, comNum);

        // 是否存在
        List<WspGiftCard> cardList = cardMapper.selectList(cardWrapper);
        if(cardList.size() == 0){
            return false;
        }
        return true;
    }


    /** 登录用 */
    // 校验卡号是否存在登录判断
    @Override
    public boolean isExistLogin(String cardNum){
        LambdaQueryWrapper<WspGiftCard> cardWrapper = new LambdaQueryWrapper<>();
        cardWrapper.ne(WspGiftCard::getIsDel,"1");//没删除
        cardWrapper.eq(WspGiftCard::getGiftCardNum, cardNum); //指定卡号

        // 新：过期也能登录。不能单单从isTrue判断，有可能长时间没有人调用礼品卡list，导致过期的礼品卡的isTrue属性得不到刷新
        //cardWrapper.ne(WspGiftCard::getIsTrue,"0");

        // 是否存在
        List<WspGiftCard> cardList = cardMapper.selectList(cardWrapper);
        if(cardList.size() == 0){
            return false;
        }
        return true;
    }


    /**
     * 迭代：查询礼品卡所有信息
     */
    public PageVo<GiftCardVo> queryCardInfoList(GiftCardPageVo pageVo){
        log.info("查询礼品卡所有信息 - {}",pageVo);

        LambdaQueryWrapper<WspGiftCard> cardWrapper = new LambdaQueryWrapper<>();
        // 匹配所有卡
        cardWrapper.ne(WspGiftCard::getIsDel,"1");
        cardWrapper.ne(WspGiftCard::getIsTrue,"0");
        // 条件查询
        if(!ObjectUtils.isEmpty(pageVo.getGiftCardNum())){
            cardWrapper.like(WspGiftCard::getGiftCardNum, pageVo.getGiftCardNum());
        }
        if(!ObjectUtils.isEmpty(pageVo.getComNum())){
            cardWrapper.eq(WspGiftCard::getComNum, pageVo.getComNum());
        }
        /** 迭代：筛选时礼品卡种类名改成礼品卡类型ID */
        if(StringUtils.isNotEmpty(pageVo.getCardTypeId())){
            cardWrapper.eq(WspGiftCard::getCardTypeId, pageVo.getCardTypeId());
        }
        if(!ObjectUtils.isEmpty(pageVo.getValidDate())){
            cardWrapper.eq(WspGiftCard::getValidDate, pageVo.getValidDate());
        }
        if(!ObjectUtils.isEmpty(pageVo.getQuotaMultiple())){
            cardWrapper.eq(WspGiftCard::getQuotaMultiple, pageVo.getQuotaMultiple());
        }
        if(!ObjectUtils.isEmpty(pageVo.getIsTrue())){
            cardWrapper.eq(WspGiftCard::getIsTrue,pageVo.getIsTrue());
        }
        if(!ObjectUtils.isEmpty(pageVo.getIsUse())){
            cardWrapper.eq(WspGiftCard::getIsUse, pageVo.getIsUse());
        }
        if(!StringUtils.isEmpty(pageVo.getUserPhone())){
            cardWrapper.like(WspGiftCard::getUserPhone, pageVo.getUserPhone());
        }

        PageHelper.startPage(pageVo.getPageNum(), pageVo.getPageSize());
        List<WspGiftCard> cardList = cardMapper.selectList(cardWrapper);
        PageInfo<WspGiftCard> tempPageInfo = new PageInfo<>(cardList);
        long total = tempPageInfo.getTotal();
        // log.info("cardList - {} ", cardList);
        // log.info("cardList.size - {} ", cardList.size());

        ArrayList<GiftCardVo> giftCardVos = new ArrayList<>();

        LambdaQueryWrapper<WspCardType> typeWrapper = new LambdaQueryWrapper<>();
        List<WspCardType> typeList = typeMapper.selectList(typeWrapper);

        for(WspCardType type: typeList){
            for(WspGiftCard card: cardList){
                // 根据卡类别id匹配 (null point 先不处理了，，，，
                if(type.getId().equals(card.getCardTypeId())){
                    GiftCardVo vo = new GiftCardVo();
                    vo.setCardName(type.getCardTypeName());
                    // log.info("foreach - {}",card.getGiftCardNum());
                    vo.setGiftCardId(card.getGiftCardId());
                    vo.setGiftCardNum(card.getGiftCardNum());
                    vo.setQuotaMultiple(card.getQuotaMultiple());
                    vo.setValidDate(card.getValidDate());
                    vo.setIsTrue(card.getIsTrue());
                    vo.setIsUse(card.getIsUse());
                    vo.setUserId(card.getUserId());
                    vo.setUserPhone(card.getUserPhone());
                    vo.setComNum(card.getComNum());
                    // add进全部礼品卡信息list
                    //log.info("循环的cardList个体 num - {}",vo.getGiftCardNum());
                    giftCardVos.add(vo);
                }
            }
        }
        PageInfo<GiftCardVo> pageInfo = new PageInfo<>(giftCardVos);
        PageVo<GiftCardVo> pageVos = new PageVo<>(pageInfo.getPageNum(), pageInfo.getPageSize(), giftCardVos, total);
        return pageVos;
    }



//    public WspGiftCard getGiftCard(String giftCardId) {
//        log.info("---查余额---");
//        LambdaQueryWrapper<WspGiftCard> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(WspGiftCard::getGiftCardId, giftCardId);
//        WspGiftCard card = new WspGiftCard();
//        card = cardMapper.selectOne(queryWrapper);
//        return card;
//    }



    /**
     * 迭代：余额（根据用户id查有效的，未使用的 礼品卡 all info）
     * */
    public HashMap<String, Object> queryCardByUserId(String id){
        log.info("---查余额---");
        HashMap<String, Object> map = new HashMap<>();
        LambdaQueryWrapper<WspGiftCard> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WspGiftCard::getUserId,id);
        queryWrapper.ne(WspGiftCard::getIsDel,"1");
        queryWrapper.eq(WspGiftCard::getIsUse,"1");// 已兑换
        queryWrapper.ne(WspGiftCard::getIsTrue,"0");
        // 根据userid查询有效的，未使用的 礼品卡
        WspGiftCard card;
        try{
            card = cardMapper.selectOne(queryWrapper);
        }catch (Exception e){
            log.info("---余额：不唯一---");
            return map;
        }

        /* 如果存在礼品卡，则去查额度 */
        if(ObjectUtil.isEmpty(card)) return map;
        List<CardTypeQuota> ctqList = quotaService.queryQuotaListByCom(card.getComNum(),card.getCardTypeId());
        if(ObjectUtil.isEmpty(ctqList)) return map;
        map.put("cardTypeName", ctqList.get(0).getCardTypeName());
        map.put("cardNum", card.getGiftCardNum());
        map.put("cardId", card.getGiftCardId());
        map.put("quotaMultiple", card.getQuotaMultiple());
        map.put("validDate", card.getValidDate());
        // 组装额度
        ArrayList<Object> quotaList = new ArrayList<>();
        ctqList.forEach(ctq->{
            HashMap<String, String> map1 = new HashMap<>();
            map1.put("goodsType", ctq.getGoodsType());
            map1.put("typeQuota", ctq.getTypeQuota()+"");
            map1.put("goodsTypeName", ctq.getDictName());
            quotaList.add(map1);
        });
        map.put("typeQuotaVoList", quotaList);

        return map;
    }


    /*
    * 下过订单之后把礼品卡isUse置2
    * cardId 礼品卡id
    * */
    public int isUse(Integer cardId){
        WspGiftCard card = new WspGiftCard();
        card.setGiftCardId(cardId);
        card.setIsUse("2");
        int i = cardMapper.updateById(card);
        return i;
    }


    /**
     * 迭代
     * 每天4点半判断是否到期
     * */
    @Scheduled(cron = "0 0 0 * * ?")
    public void overdueHandler(){
        LambdaQueryWrapper<WspGiftCard> wrapper = new LambdaQueryWrapper<>();
        // wrapper.ne(WspGiftCard::getIsDel, "1");
        List<WspGiftCard> cardList = cardMapper.selectList(wrapper);
        cardList.forEach(card -> {
            changeValid(card.getGiftCardId(), card.getValidDate());
        });
    }


    /**
     * 迭代修改：判断是否过期, wsp_gift_card表更新isTrue字段, 返回isTrue（参数 date为该卡的有效期）
     */
    public String changeValid(String date){

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date parse = null;
        Date today = new Date();
        try {
            parse = format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // 如果是同一天 则设置有效
        boolean sameDay = DateUtils.isSameDay(today, parse);
        WspGiftCard card = new WspGiftCard();
        if(sameDay){
            // 同一天 设置1
            return "1";
        }else{
            /*
             * 如果参数 Date 等于此 Date，则返回值 0；
             * 如果此 Date 在 Date 参数之前，则返回小于 0 的值；
             * 如果此 Date 在 Date 参数之后，则返回大于 0 的值。
             * */
            // 这个方法不好处理 同一天的情况 ，所以用了DateUtils.isSameDay(today, parse);
            int i = parse.compareTo(today);
            if(i>0){
                // 没过期 设置1
                return "1";
            }else{
                // 到期了且不是同一天 设置0
                return "0";
            }
        }
    }


























































    // 判断是否过期, wsp_gift_card表更新isTrue字段, 返回isTrue（参数 date为该卡的有效期）
    public String changeValid(Integer cardId, String date){

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date parse = null;
        Date today = new Date();
        try {
            parse = format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // 如果是同一天 则设置有效
        boolean sameDay = DateUtils.isSameDay(today, parse);
        WspGiftCard card = new WspGiftCard();
        if(sameDay){
            // 同一天 设置1
            card.setGiftCardId(cardId);
            card.setIsTrue("1");
            super.updateById(card);
            return "1";
        }else{
            /*
            * 如果参数 Date 等于此 Date，则返回值 0；
            * 如果此 Date 在 Date 参数之前，则返回小于 0 的值；
            * 如果此 Date 在 Date 参数之后，则返回大于 0 的值。
            * */
            // 这个方法不好处理 同一天的情况 ，所以用了DateUtils.isSameDay(today, parse);
            int i = parse.compareTo(today);
            if(i>0){
                // 没过期 设置1
                card.setGiftCardId(cardId);
                card.setIsTrue("1");
                super.updateById(card);
                return "1";
            }else{
                // 到期了且不是同一天 设置0
                card.setGiftCardId(cardId);
                card.setIsTrue("0");
                super.updateById(card);
                return "0";
            }
        }
    }































































    @Override
    public WspGiftCard getInfoById(String id) {
        return super.getById(id);
    }

    @Override
    public boolean save(WspGiftCardVO dto) {
        WspGiftCard entity = new WspGiftCard();
        BeanUtils.copyProperties(dto, entity);
        return super.save(entity);
    }

    @Override
    public boolean updateById(WspGiftCardVO dto) {
        WspGiftCard entity = new WspGiftCard();
        BeanUtils.copyProperties(dto, entity);
        return super.updateById(entity);
    }

    @Override
    public boolean removeById(Long id) {
        return super.removeById(id);
    }

    @Override
    public IPage<WspGiftCard> page(WspGiftCardVO dto) {
        LambdaQueryWrapper<WspGiftCard> queryWrapper = new LambdaQueryWrapper<>();
        if (ObjectUtil.isNotNull(dto)) {

        }
        Page<WspGiftCard> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        return this.page(page, queryWrapper);
    }



}
