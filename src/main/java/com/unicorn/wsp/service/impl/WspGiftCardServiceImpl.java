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
import com.unicorn.wsp.entity.*;
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
import com.unicorn.wsp.vo.LoginVo;
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
    *  ?????????
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


    // ??????????????????????????????
    public boolean reSetCard(String cardNum){
        LambdaQueryWrapper<WspGiftCard> wrapper = new LambdaQueryWrapper<>();
        if(StringUtils.isEmpty(cardNum)){
            return false;
        }
        //wrapper.eq(WspGiftCard::getGiftCardNum,cardNum);
        wrapper.eq(WspGiftCard::getGiftCardId,cardNum);
        wrapper.ne(WspGiftCard::getIsDel, "1");// ????????????
        wrapper.eq(WspGiftCard::getIsUse,"2");// ????????????
        wrapper.eq(WspGiftCard::getIsTrue,"1");// ?????????

        WspGiftCard card = new WspGiftCard();
        card.setIsUse("1");
        int update = cardMapper.update(card, wrapper);

        if(update > 0){
            return true;
        }
        return false;
    }

    // ?????? ??????????????????????????????
    public Integer onlyOneCard(String userId){
        LambdaQueryWrapper<WspGiftCard> wrapper = new LambdaQueryWrapper<>();
        wrapper.ne(WspGiftCard::getIsDel, "1");// ????????????
        wrapper.eq(WspGiftCard::getUserId, userId);// ????????????
        wrapper.eq(WspGiftCard::getIsTrue,"1");// ??????
        wrapper.eq(WspGiftCard::getIsUse,"1");// ?????????
        WspGiftCard card = cardMapper.selectOne(wrapper);
        if(ObjectUtil.isNull(card)){
            return 0;
        }
        return 1;
    }

    // ?????? ??????????????????????????????
    public Integer onlyOneCard(LoginVo loginVo, String userId){
        LambdaQueryWrapper<WspGiftCard> wrapper = new LambdaQueryWrapper<>();
        wrapper.ne(WspGiftCard::getIsDel, "1");// ????????????
        wrapper.eq(WspGiftCard::getUserId, loginVo.getUserPhone());// ????????????
        wrapper.eq(WspGiftCard::getIsTrue,"1");// ??????
        wrapper.eq(WspGiftCard::getIsUse,"1");// ?????????
        wrapper.eq(WspGiftCard::getUserPhone,userId);// ?????????
        wrapper.eq(WspGiftCard::getGiftCardNum,loginVo.getGiftCardNum());// ?????????
        wrapper.eq(WspGiftCard::getComNum,loginVo.getUserCom());// ?????????

        WspGiftCard card = cardMapper.selectOne(wrapper);
        if(ObjectUtil.isNull(card)){
            return 0;
        }
        return 1;
    }


    // ??????????????????
    public int exchange(String cardNum,String userId,String phone){
        log.info("???????????????????????? - {} , {}",cardNum,userId);
        LambdaQueryWrapper<WspGiftCard> cardWrapper = new LambdaQueryWrapper<>();
        cardWrapper.eq(WspGiftCard::getGiftCardNum,cardNum);
        cardWrapper.eq(WspGiftCard::getIsUse,"0");
        WspGiftCard card = new WspGiftCard();
        card.setIsUse("1");
        card.setUserId(userId);
        card.setUserPhone(phone);
        log.info("?????????????????????dto - {}",card);
        return cardMapper.update(card, cardWrapper);
    }


    /**
     * ???????????????????????????
     * */
    public boolean unbound(int giftCardId){
        LambdaQueryWrapper<WspGiftCard> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WspGiftCard::getGiftCardId,giftCardId);
        wrapper.ne(WspGiftCard::getIsDel, "1");// ????????????
        wrapper.eq(WspGiftCard::getIsUse,"1");// ?????????????????????
        wrapper.eq(WspGiftCard::getIsTrue,"1");// ?????????
        WspGiftCard card = new WspGiftCard();
        card.setIsUse("0");
        card.setUserPhone("");
        card.setUserId("");
        int update = cardMapper.update(card, wrapper);
        if(update>1) {
            log.info("???????????????????????????-giftCardId=: {}",giftCardId);
            throw new BusinessRollbackException("???????????????????????????");
        }
        return update == 1;
    }


    /** ?????????????????????*/
    // ????????????????????????
    public boolean isExist(String cardNum){
        LambdaQueryWrapper<WspGiftCard> cardWrapper = new LambdaQueryWrapper<>();
        cardWrapper.ne(WspGiftCard::getIsDel,"1");//?????????
        cardWrapper.eq(WspGiftCard::getIsUse,"0");// ?????????
        cardWrapper.eq(WspGiftCard::getGiftCardNum, cardNum); //????????????

        // ???????????????isTrue???????????????????????????????????????????????????list??????????????????????????????isTrue?????????????????????
        cardWrapper.ne(WspGiftCard::getIsTrue,"0");

        // ????????????
        List<WspGiftCard> cardList = cardMapper.selectList(cardWrapper);
        if(cardList.size() == 0){
            return false;
        }

        // ????????????
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
     * ?????????
     * ?????????????????????????????????????????????????????????????????????
     * */
    public boolean isExist(String cardNum, String comNum){
        LambdaQueryWrapper<WspGiftCard> cardWrapper = new LambdaQueryWrapper<>();
        cardWrapper.ne(WspGiftCard::getIsDel,"1");//?????????
        cardWrapper.eq(WspGiftCard::getIsUse,"0");// ?????????
        cardWrapper.eq(WspGiftCard::getGiftCardNum, cardNum); //????????????
        /** ?????????*/
        cardWrapper.eq(WspGiftCard::getIsTrue, "1");
        /** ????????????*/
        cardWrapper.eq(WspGiftCard::getComNum, comNum);

        // ???????????????isTrue???????????????????????????????????????????????????list??????????????????????????????isTrue?????????????????????
        // ?????????
        cardWrapper.ne(WspGiftCard::getIsTrue,"0");

        // ????????????
        List<WspGiftCard> cardList = cardMapper.selectList(cardWrapper);
        if(cardList.size() == 0){
            return false;
        }

        // ????????????(-- ?????????sql??????isTrue?????? --)
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
     * ?????????
     * ???????????????????????????????????????????????????
     * */
    public boolean isExistLogin(String cardNum, String comNum){
        LambdaQueryWrapper<WspGiftCard> cardWrapper = new LambdaQueryWrapper<>();
        cardWrapper.ne(WspGiftCard::getIsDel,"1");//?????????
        cardWrapper.eq(WspGiftCard::getGiftCardNum, cardNum); //????????????
        /** ????????????*/
        cardWrapper.eq(WspGiftCard::getComNum, comNum);

        // ????????????
        List<WspGiftCard> cardList = cardMapper.selectList(cardWrapper);
        if(cardList.size() == 0){
            return false;
        }
        return true;
    }


    /** ????????? */
    // ????????????????????????????????????
    @Override
    public boolean isExistLogin(String cardNum){
        LambdaQueryWrapper<WspGiftCard> cardWrapper = new LambdaQueryWrapper<>();
        cardWrapper.ne(WspGiftCard::getIsDel,"1");//?????????
        cardWrapper.eq(WspGiftCard::getGiftCardNum, cardNum); //????????????

        // ??????????????????????????????????????????isTrue???????????????????????????????????????????????????list??????????????????????????????isTrue?????????????????????
        //cardWrapper.ne(WspGiftCard::getIsTrue,"0");

        // ????????????
        List<WspGiftCard> cardList = cardMapper.selectList(cardWrapper);
        if(cardList.size() == 0){
            return false;
        }
        return true;
    }


    /**
     * ????????????????????????????????????
     */
    public PageVo<GiftCardVo> queryCardInfoList(GiftCardPageVo pageVo){
        log.info("??????????????????????????? - {}",pageVo);

        LambdaQueryWrapper<WspGiftCard> cardWrapper = new LambdaQueryWrapper<>();
        // ???????????????
        cardWrapper.ne(WspGiftCard::getIsDel,"1");
        cardWrapper.ne(WspGiftCard::getIsTrue,"0");
        // ????????????
        if(!ObjectUtils.isEmpty(pageVo.getGiftCardNum())){
            cardWrapper.like(WspGiftCard::getGiftCardNum, pageVo.getGiftCardNum());
        }
        if(!ObjectUtils.isEmpty(pageVo.getComNum())){
            cardWrapper.eq(WspGiftCard::getComNum, pageVo.getComNum());
        }
        /** ?????????????????????????????????????????????????????????ID */
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
                // ???????????????id?????? (null point ???????????????????????????
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
                    // add????????????????????????list
                    //log.info("?????????cardList?????? num - {}",vo.getGiftCardNum());
                    giftCardVos.add(vo);
                }
            }
        }
        PageInfo<GiftCardVo> pageInfo = new PageInfo<>(giftCardVos);
        PageVo<GiftCardVo> pageVos = new PageVo<>(pageInfo.getPageNum(), pageInfo.getPageSize(), giftCardVos, total);
        return pageVos;
    }



//    public WspGiftCard getGiftCard(String giftCardId) {
//        log.info("---?????????---");
//        LambdaQueryWrapper<WspGiftCard> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(WspGiftCard::getGiftCardId, giftCardId);
//        WspGiftCard card = new WspGiftCard();
//        card = cardMapper.selectOne(queryWrapper);
//        return card;
//    }



    /**
     * ??????????????????????????????id??????????????????????????? ????????? all info???
     * */
    public HashMap<String, Object> queryCardByUserId(String id){
        log.info("---?????????---");
        HashMap<String, Object> map = new HashMap<>();
        LambdaQueryWrapper<WspGiftCard> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WspGiftCard::getUserId,id);
        queryWrapper.ne(WspGiftCard::getIsDel,"1");
        queryWrapper.eq(WspGiftCard::getIsUse,"1");// ?????????
        queryWrapper.ne(WspGiftCard::getIsTrue,"0");
        // ??????userid?????????????????????????????? ?????????
        WspGiftCard card;
        try{
            card = cardMapper.selectOne(queryWrapper);
        }catch (Exception e){
            log.info("---??????????????????---");
            return map;
        }

        /* ??????????????????????????????????????? */
        if(ObjectUtil.isEmpty(card)) return map;
        List<CardTypeQuota> ctqList = quotaService.queryQuotaListByCom(card.getComNum(),card.getCardTypeId());
        if(ObjectUtil.isEmpty(ctqList)) return map;
        map.put("cardTypeName", ctqList.get(0).getCardTypeName());
        map.put("cardNum", card.getGiftCardNum());
        map.put("cardId", card.getGiftCardId());
        map.put("quotaMultiple", card.getQuotaMultiple());
        map.put("validDate", card.getValidDate());
        // ????????????
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
    * ??????????????????????????????isUse???2
    * cardId ?????????id
    * */
    public int isUse(Integer cardId){
        WspGiftCard card = new WspGiftCard();
        card.setGiftCardId(cardId);
        card.setIsUse("2");
        int i = cardMapper.updateById(card);
        return i;
    }


    /**
     * ??????
     * ??????4????????????????????????
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
     * ?????????????????????????????????, wsp_gift_card?????????isTrue??????, ??????isTrue????????? date????????????????????????
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

        // ?????????????????? ???????????????
        boolean sameDay = DateUtils.isSameDay(today, parse);
        WspGiftCard card = new WspGiftCard();
        if(sameDay){
            // ????????? ??????1
            return "1";
        }else{
            /*
             * ???????????? Date ????????? Date??????????????? 0???
             * ????????? Date ??? Date ?????????????????????????????? 0 ?????????
             * ????????? Date ??? Date ?????????????????????????????? 0 ?????????
             * */
            // ???????????????????????? ?????????????????? ???????????????DateUtils.isSameDay(today, parse);
            int i = parse.compareTo(today);
            if(i>0){
                // ????????? ??????1
                return "1";
            }else{
                // ??????????????????????????? ??????0
                return "0";
            }
        }
    }


























































    // ??????????????????, wsp_gift_card?????????isTrue??????, ??????isTrue????????? date????????????????????????
    public String changeValid(Integer cardId, String date){

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date parse = null;
        Date today = new Date();
        try {
            parse = format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // ?????????????????? ???????????????
        boolean sameDay = DateUtils.isSameDay(today, parse);
        WspGiftCard card = new WspGiftCard();
        if(sameDay){
            // ????????? ??????1
            card.setGiftCardId(cardId);
            card.setIsTrue("1");
            super.updateById(card);
            return "1";
        }else{
            /*
            * ???????????? Date ????????? Date??????????????? 0???
            * ????????? Date ??? Date ?????????????????????????????? 0 ?????????
            * ????????? Date ??? Date ?????????????????????????????? 0 ?????????
            * */
            // ???????????????????????? ?????????????????? ???????????????DateUtils.isSameDay(today, parse);
            int i = parse.compareTo(today);
            if(i>0){
                // ????????? ??????1
                card.setGiftCardId(cardId);
                card.setIsTrue("1");
                super.updateById(card);
                return "1";
            }else{
                // ??????????????????????????? ??????0
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
