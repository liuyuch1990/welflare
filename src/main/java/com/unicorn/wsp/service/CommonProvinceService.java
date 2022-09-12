package com.unicorn.wsp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.unicorn.wsp.entity.CommonProvince;
import com.unicorn.wsp.entity.vo.CommonProvinceVO;
import com.unicorn.wsp.mapper.CommonProvinceMapper;
import com.unicorn.wsp.common.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
/**
* <p>
    *  服务接口
    * </p>
*
* @author spark
* @since 1.0
*/
@Service
public interface CommonProvinceService extends IService<CommonProvince>  {

    CommonProvince getInfoById(Long id);

    boolean save(CommonProvinceVO dto);

    boolean updateById(CommonProvinceVO dto);

    boolean removeById(Long id);

    IPage<CommonProvince> page(CommonProvinceVO dto);


}

