package com.unicorn.wsp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.unicorn.wsp.entity.CommonArea;
import com.unicorn.wsp.entity.vo.CommonAreaVO;
import com.unicorn.wsp.mapper.CommonAreaMapper;
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
public interface CommonAreaService extends IService<CommonArea>  {

    CommonArea getInfoById(Long id);

    boolean save(CommonAreaVO dto);

    boolean updateById(CommonAreaVO dto);

    boolean removeById(Long id);

    IPage<CommonArea> page(CommonAreaVO dto);


}

