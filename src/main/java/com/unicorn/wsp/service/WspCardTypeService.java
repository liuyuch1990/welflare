package com.unicorn.wsp.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.unicorn.wsp.entity.WspCardType;
import com.unicorn.wsp.entity.vo.WspCardQuotaVO;
import com.unicorn.wsp.entity.vo.WspCardTypeVO;
import com.unicorn.wsp.entity.zznvo.WspCardQuotaPageVo;
import com.unicorn.wsp.mapper.WspCardTypeMapper;
import com.unicorn.wsp.common.result.Result;
import com.unicorn.wsp.vo.GiftCardTypeVo;
import com.unicorn.wsp.vo.PageVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* <p>
    *  服务接口
    * </p>
*
* @author spark
* @since 1.0
*/
@Service
public interface WspCardTypeService extends IService<WspCardType>  {

    WspCardType getInfoById(Long id);

    boolean save(WspCardTypeVO dto);

    boolean updateById(WspCardTypeVO dto);

    boolean removeById(Long id);

    IPage<WspCardType> page(WspCardTypeVO dto);

    /**
     * 分页，条件，查询礼品卡种类列表
     * */
    PageVo<Map<String, Object>> queryCardTypeList(WspCardQuotaPageVo vo);


    /**
     * 迭代：导入礼品卡时额度String List
     * */
    List<String> queryCardTypeList(String com);


    /**
     * 迭代：导入礼品卡时额度下拉
     * */
    List<WspCardType> queryCardTypeDropDownList(String com);


    /**
     * 迭代修改额度
     * */
    boolean updateQuota(GiftCardTypeVo dto);

    /**
     * 迭代：删除礼品卡种类
     * */
    boolean deleteCardType(String cardTypeId);


    /**
     * 迭代：礼品卡种类详情
     * */
    HashMap<String, Object> queryCardTypeById(String cardTypeId);


    /**
     * 迭代 检查礼品卡重名
     * */
    boolean onlyName(String cardName);

    /**
     * 迭代 检查礼品卡重名(编辑，排除原来礼品卡)
     * */
    boolean onlyNameExcludeOldName(String cardName,String id);

    /**
     * 迭代 新建礼品卡种类
     * */
    void createCard(GiftCardTypeVo vo);

}

