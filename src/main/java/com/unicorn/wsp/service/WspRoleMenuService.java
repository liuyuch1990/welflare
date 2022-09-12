package com.unicorn.wsp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.unicorn.wsp.entity.WspRoleMenu;
import com.unicorn.wsp.entity.vo.WspRoleMenuVO;
import com.unicorn.wsp.mapper.WspRoleMenuMapper;
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
public interface WspRoleMenuService extends IService<WspRoleMenu>  {

    WspRoleMenu getInfoById(Long id);

    boolean save(WspRoleMenuVO dto);

    boolean updateById(WspRoleMenuVO dto);

    boolean removeById(Long id);

    IPage<WspRoleMenu> page(WspRoleMenuVO dto);


}

