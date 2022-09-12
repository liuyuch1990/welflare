package com.unicorn.wsp.service.impl;



import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.unicorn.wsp.common.exception.BusinessRollbackException;
import com.unicorn.wsp.entity.WspCardQuota;
import com.unicorn.wsp.entity.WspCardType;
import com.unicorn.wsp.entity.vo.WspCardTypeVO;
import com.unicorn.wsp.entity.zznvo.WspCardQuotaPageVo;
import com.unicorn.wsp.mapper.WspCardQuotaMapper;
import com.unicorn.wsp.mapper.WspCardTypeMapper;
import com.unicorn.wsp.service.WspCardQuotaService;
import com.unicorn.wsp.service.WspCardTypeService;
import com.unicorn.wsp.vo.CardTypeQuota;
import com.unicorn.wsp.vo.GiftCardTypeVo;
import com.unicorn.wsp.vo.PageVo;
import com.unicorn.wsp.vo.TypeQuoteVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
* <p>
    *  服务类
    * </p>
*
* @author spark
* @since 1.0
*/
@Slf4j
@Service
public class WspCardTypeServiceImpl extends ServiceImpl<WspCardTypeMapper,WspCardType>
    implements WspCardTypeService {

    @Autowired
    WspCardTypeMapper cardTypeMapper;

    @Autowired
    WspCardQuotaService quotaService;

    @Autowired
    WspCardQuotaMapper quotaMapper;




    /**
     * 迭代:分页，条件，查询礼品卡种类列表
     * */
    public PageVo<Map<String, Object>> queryCardTypeList(WspCardQuotaPageVo vo){

        LambdaQueryWrapper<WspCardType> cardTypeWrapper = new LambdaQueryWrapper<>();

        cardTypeWrapper.eq(WspCardType::getComNum, vo.getComNum());
        if(StringUtils.isNotEmpty(vo.getCardTypeName())) {
            cardTypeWrapper.like(WspCardType::getCardTypeName, vo.getCardTypeName());
        }

        // 根据type数量分页
        PageHelper.startPage(vo.getPageNum(), vo.getPageSize());
        List<WspCardType> typeStrList = cardTypeMapper.selectList(cardTypeWrapper);
        log.debug("-----------"+typeStrList.size());
        List<Map<String, Object>> mapList = new ArrayList<>();
        typeStrList.forEach(type->{
            HashMap<String, Object> map = new HashMap<>();
            map.put("cardTypeId", type.getId());
            map.put("cardTypeName", type.getCardTypeName());
            ArrayList<Object> quotaList = new ArrayList<>();
            List<CardTypeQuota> ctqList = quotaService.queryQuotaListByCom(type.getComNum(),type.getId());
            ctqList.forEach(ctq->{
                HashMap<String, Object> map1 = new HashMap<>();
                map1.put("goodsType", ctq.getGoodsType());
                map1.put("typeQuota", ctq.getTypeQuota());
                map1.put("dictName", ctq.getDictName());
                map1.put("goodsTypeName", ctq.getDictName());
                quotaList.add(map1);
            });
            map.put("typeQuotaVoList", quotaList);
            mapList.add(map);
        });

        // 为了拿到正确的pageTotal，临时代替pageInfo.getTotal
        PageInfo<WspCardType> pageInfo = new PageInfo<>(typeStrList);
        PageVo<Map<String, Object>> pageVo = new PageVo<>(pageInfo.getPageNum(), pageInfo.getSize(), mapList, pageInfo.getTotal());

        return pageVo;
    }

    /**
     * 迭代：导入礼品卡时额度String List
     * */
    public List<String> queryCardTypeList(String com){
        LambdaQueryWrapper<WspCardType> wspCardQuotaLambdaQueryWrapper = new LambdaQueryWrapper<>();
        wspCardQuotaLambdaQueryWrapper.eq(WspCardType::getComNum, com);
        List<WspCardType> wspCardQuotas = cardTypeMapper.selectList(wspCardQuotaLambdaQueryWrapper);
        log.info("额度种类查出 - {}",wspCardQuotas);
        List<String> list = new ArrayList<>();
        for(WspCardType t: wspCardQuotas){
            list.add(t.getId());
        }
        log.info("额度下拉 - {}",list);
        return list;
    }

    /**
     * 迭代：导入礼品卡时额度下拉
     * */
    public List<WspCardType> queryCardTypeDropDownList(String com){
        LambdaQueryWrapper<WspCardType> wspCardQuotaLambdaQueryWrapper = new LambdaQueryWrapper<>();
        wspCardQuotaLambdaQueryWrapper.eq(WspCardType::getComNum, com);
        List<WspCardType> wspCardQuotas = cardTypeMapper.selectList(wspCardQuotaLambdaQueryWrapper);
        log.info("额度种类查出 - {}",wspCardQuotas);
        return wspCardQuotas;
    }



    /**
     * 迭代 新建礼品卡种类
     * */
    @Transactional
    public void createCard(GiftCardTypeVo vo) {
        // insert card_type
        WspCardType cardType = new WspCardType();
        BeanUtils.copyProperties(vo, cardType);
        String cardTypeId = UUID.randomUUID().toString().replaceAll("-", "");
        cardType.setId(cardTypeId);
        int insert = cardTypeMapper.insert(cardType);
        if (insert != 1) throw new BusinessRollbackException("新建礼品卡异常");

        // insert card_quota
        List<TypeQuoteVo> typeQuoteVoList = vo.getTypeQuotaVoList();
        insertBatchQuota(typeQuoteVoList, cardTypeId);

    }


    /*
    * 新增：insert card_quota表
    * */
    @Transactional
    void insertBatchQuota(List<TypeQuoteVo> typeQuoteVoList, String cardTypeId){
        typeQuoteVoList.forEach(typeQuota ->{
            WspCardQuota addQuota = new WspCardQuota();
            addQuota.setCardTypeId(cardTypeId);
            addQuota.setTypeQuota(typeQuota.getTypeQuota());
            addQuota.setGoodsType(typeQuota.getGoodsType());
            int insert1 = quotaMapper.insert(addQuota);
            if(insert1 != 1) throw new BusinessRollbackException("礼品卡映射额度异常");
        });
    }


    /**
     * 迭代 检查礼品卡重名
     * */
    public boolean onlyName(String cardName){
        LambdaQueryWrapper<WspCardType> cardTypeWrapper = new LambdaQueryWrapper<>();
        cardTypeWrapper.eq(WspCardType::getCardTypeName, cardName);
        Integer count = cardTypeMapper.selectCount(cardTypeWrapper);
        return count != 0;
    }

    /**
     * 迭代 检查礼品卡重名(用于编辑时排除原来礼品卡名)
     * */
    public boolean onlyNameExcludeOldName(String cardName,String id){
        LambdaQueryWrapper<WspCardType> cardTypeWrapper = new LambdaQueryWrapper<>();
        cardTypeWrapper.eq(WspCardType::getCardTypeName, cardName);
        cardTypeWrapper.ne(WspCardType::getId, id);
        Integer count = cardTypeMapper.selectCount(cardTypeWrapper);
        return count != 0;
    }



    /**
     * 迭代修改礼品卡种类额度
     * */
    @Transactional
    public boolean updateQuota(GiftCardTypeVo dto){
        /*修改名称*/
        WspCardType wspCardType = new WspCardType();
        wspCardType.setCardTypeName(dto.getCardTypeName());
        wspCardType.setId(dto.getCardTypeId());
        LambdaQueryWrapper<WspCardType> cardTypeWrapper = new LambdaQueryWrapper<>();
        int i = cardTypeMapper.updateById(wspCardType);
        if(i!=1) throw new BusinessRollbackException("礼品卡类型数据异常");
        /*修改额度*/
        // 删旧quota
        boolean b = deleteQuota(dto.getCardTypeId());
        if(!b) throw new BusinessRollbackException("礼品卡额度数据异常");
        // 添 新quota
        insertBatchQuota(dto.getTypeQuotaVoList(), dto.getCardTypeId());

        return true;
    }



    /**
     * 迭代：删除礼品卡种类
     * */
    @Transactional
    public boolean deleteCardType(String cardTypeId){
        /*删礼品卡type*/
        boolean b = deleteType(cardTypeId);
        if(!b) throw new BusinessRollbackException("礼品卡类型不存在");
        /*删礼品卡额度*/
        boolean b2 = deleteQuota(cardTypeId);
        if(!b2) throw new BusinessRollbackException("礼品卡额度数据异常");

        return true;
    }


    /*
    * 删礼品卡type
    * */
    boolean deleteType(String cardId){
        LambdaQueryWrapper<WspCardType> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WspCardType::getId, cardId);
        int delete = cardTypeMapper.delete(queryWrapper);
        return delete ==1;
    }


    /*
    * 删礼品卡额度
    * */
    boolean deleteQuota(String cardId){
        LambdaQueryWrapper<WspCardQuota> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WspCardQuota::getCardTypeId, cardId);
        int delete = quotaMapper.delete(queryWrapper);
        return delete > 0;
    }



    /**
     * 迭代：礼品卡种类详情
     * */
    public HashMap<String, Object> queryCardTypeById(String cardTypeId){
        LambdaQueryWrapper<WspCardType> cardTypeWrapper = new LambdaQueryWrapper<>();
        cardTypeWrapper.eq(WspCardType::getId, cardTypeId);
        WspCardType type = cardTypeMapper.selectOne(cardTypeWrapper);

        HashMap<String, Object> map = new HashMap<>();
        map.put("cardTypeId", type.getId());
        map.put("cardTypeName", type.getCardTypeName());
        ArrayList<Object> quotaList = new ArrayList<>();
        List<CardTypeQuota> ctqList = quotaService.queryQuotaListByCom(type.getComNum(),type.getId());
        ctqList.forEach(ctq->{
            HashMap<String, Object> map1 = new HashMap<>();
            map1.put("goodsType", ctq.getGoodsType());
            map1.put("typeQuota", ctq.getTypeQuota());
            map1.put("goodsTypeName", ctq.getDictName());
            quotaList.add(map1);
        });
        map.put("typeQuotaVoList", quotaList);
        return map;
    }

























    @Override
    public WspCardType getInfoById(Long id) {
        return super.getById(id);
    }

    @Override
    public boolean save(WspCardTypeVO dto) {
        WspCardType entity = new WspCardType();
        BeanUtils.copyProperties(dto, entity);
        return super.save(entity);
    }

    @Override
    public boolean updateById(WspCardTypeVO dto) {
        WspCardType entity = new WspCardType();
        BeanUtils.copyProperties(dto, entity);
        return super.updateById(entity);
    }

    @Override
    public boolean removeById(Long id) {
        return super.removeById(id);
    }

    @Override
    public IPage<WspCardType> page(WspCardTypeVO dto) {
        LambdaQueryWrapper<WspCardType> queryWrapper = new LambdaQueryWrapper<>();
        if (ObjectUtil.isNotNull(dto)) {

        }
        Page<WspCardType> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        return this.page(page, queryWrapper);
    }
}
