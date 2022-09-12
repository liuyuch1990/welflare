package com.unicorn.wsp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.unicorn.wsp.entity.CommonCity;
import com.unicorn.wsp.entity.vo.CommonCityVO;
import com.unicorn.wsp.mapper.CommonCityMapper;
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
public interface CommonCityService extends IService<CommonCity>  {

    CommonCity getInfoById(Long id);

    boolean save(CommonCityVO dto);

    boolean updateById(CommonCityVO dto);

    boolean removeById(Long id);

    IPage<CommonCity> page(CommonCityVO dto);


}

