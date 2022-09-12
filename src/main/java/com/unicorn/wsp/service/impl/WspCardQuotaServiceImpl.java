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
import com.unicorn.wsp.entity.exportvo.WspCardQuotaETO;
import com.unicorn.wsp.entity.vo.WspCardQuotaVO;
import com.unicorn.wsp.entity.zznvo.GiftCardVo;
import com.unicorn.wsp.entity.zznvo.WspCardQuotaPageVo;
import com.unicorn.wsp.mapper.WspCardQuotaMapper;
import com.unicorn.wsp.mapper.WspCardTypeMapper;
import com.unicorn.wsp.mapper.WspGiftCardMapper;
import com.unicorn.wsp.service.SimpleDictService;
import com.unicorn.wsp.service.WspCardQuotaService;
import com.unicorn.wsp.service.WspGiftCardService;
import com.unicorn.wsp.vo.CardTypeQuota;
import com.unicorn.wsp.vo.GiftCardTypeVo;
import com.unicorn.wsp.vo.PageVo;
import com.unicorn.wsp.vo.TypeQuoteVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
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
public class WspCardQuotaServiceImpl extends ServiceImpl<WspCardQuotaMapper,WspCardQuota>
    implements WspCardQuotaService {

    @Autowired
    WspCardQuotaMapper quotaMapper;

    @Autowired
    WspGiftCardMapper cardMapper;

    @Autowired
    WspCardTypeMapper cardTypeMapper;

    @Autowired
    WspGiftCardService cardService;


    /**
     * 迭代：根据礼品卡种类id查询额度list
     * */
    public List<WspCardQuota> queryQuotaList(String cardTypeId){
        LambdaQueryWrapper<WspCardQuota> queryWrapper = new LambdaQueryWrapper<>();
        if(StringUtils.isNotEmpty(cardTypeId))
            queryWrapper.eq(WspCardQuota::getCardTypeId, cardTypeId);
        List<WspCardQuota> quotaList = quotaMapper.selectList(queryWrapper);
        return quotaList;
    }

    /**
     * 迭代: 根据公司名查额度(code,name,com,quota,typeId)
     * */
    public List<CardTypeQuota> queryQuotaListByCom(String comNum, String cardId){
        List<CardTypeQuota> list = quotaMapper.queryCardTypeQuotaList(comNum, cardId);
        return list;
    }

    /**
     * 迭代：修改有效期
     * */
    public int updateDate(WspGiftCard vo){
        if(ObjectUtil.isNull(vo.getGiftCardId())){
            return 0;
        }
        if(ObjectUtil.isNull(vo.getValidDate())){
            return 0;
        }
        LambdaQueryWrapper<WspGiftCard> wspCardQuotaLambdaQueryWrapper = new LambdaQueryWrapper<>();
        wspCardQuotaLambdaQueryWrapper.eq(WspGiftCard::getGiftCardId,vo.getGiftCardId());
        wspCardQuotaLambdaQueryWrapper.ne(WspGiftCard::getIsDel,"1");

        String isTrue = cardService.changeValid(vo.getValidDate());
        vo.setIsTrue(isTrue);

        return cardMapper.update(vo, wspCardQuotaLambdaQueryWrapper);
    }


    public WspCardQuota queryById(Integer id) {
        LambdaQueryWrapper<WspCardQuota> wspCardQuotaLambdaQueryWrapper = new LambdaQueryWrapper<>();
        wspCardQuotaLambdaQueryWrapper.eq(WspCardQuota::getQuotaId,id);
        wspCardQuotaLambdaQueryWrapper.ne(WspCardQuota::getIsDel,"1");
        WspCardQuota wspCardQuota = quotaMapper.selectOne(wspCardQuotaLambdaQueryWrapper);
        return wspCardQuota;
    }













































































    ////////////////////////////////////////////////////////////////////////////////////////////


    @Override
    public WspCardQuota getInfoById(Long id) {
        return super.getById(id);
    }

    @Override
    public boolean save(WspCardQuotaVO dto) {
        WspCardQuota entity = new WspCardQuota();
        BeanUtils.copyProperties(dto, entity);
        return super.save(entity);
    }

    @Override
    public boolean updateById(WspCardQuotaVO dto) {
        WspCardQuota entity = new WspCardQuota();
        BeanUtils.copyProperties(dto, entity);
        return super.updateById(entity);
    }

    @Override
    public boolean removeById(Long id) {
        return super.removeById(id);
    }



    @Override
    public IPage<WspCardQuota> page(WspCardQuotaVO dto) {
        return null;
    }
}
