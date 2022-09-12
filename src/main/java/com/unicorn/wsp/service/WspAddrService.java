package com.unicorn.wsp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.unicorn.wsp.entity.WspAddr;
import com.unicorn.wsp.entity.vo.WspAddrVO;
import com.unicorn.wsp.mapper.WspAddrMapper;
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
public interface WspAddrService extends IService<WspAddr>  {

    WspAddr getInfoById(Long id);

    boolean save(WspAddrVO dto);

    boolean updateById(WspAddrVO dto);

    boolean removeById(Long id);

    IPage<WspAddr> page(WspAddrVO dto);


}

