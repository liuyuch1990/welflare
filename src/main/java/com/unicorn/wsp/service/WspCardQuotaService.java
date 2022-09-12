package com.unicorn.wsp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.unicorn.wsp.entity.WspCardQuota;
import com.unicorn.wsp.entity.WspGiftCard;
import com.unicorn.wsp.entity.vo.WspCardQuotaVO;
import com.unicorn.wsp.entity.zznvo.GiftCardVo;
import com.unicorn.wsp.entity.zznvo.WspCardQuotaPageVo;
import com.unicorn.wsp.mapper.WspCardQuotaMapper;
import com.unicorn.wsp.common.result.Result;
import com.unicorn.wsp.vo.CardTypeQuota;
import com.unicorn.wsp.vo.GiftCardTypeVo;
import com.unicorn.wsp.vo.PageVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
* <p>
    *  服务接口
    * </p>
*
* @author spark
* @since 1.0
*/
@Service
public interface WspCardQuotaService extends IService<WspCardQuota>  {

    WspCardQuota getInfoById(Long id);

    boolean save(WspCardQuotaVO dto);

    boolean updateById(WspCardQuotaVO dto);

    boolean removeById(Long id);



    IPage<WspCardQuota> page(WspCardQuotaVO dto);

    WspCardQuota queryById(Integer id);



    int updateDate(WspGiftCard vo);





    /**
     * 迭代：根据礼品卡种类id查询额度list
     * */
    List<WspCardQuota> queryQuotaList(String cardTypeId);

    /**
     * 迭代: 根据公司名+cardID查额度(code,name,com,quota,typeId)
     * */
    List<CardTypeQuota> queryQuotaListByCom(String comNum, String cardId);


}

