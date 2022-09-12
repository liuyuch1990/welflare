package com.unicorn.wsp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.unicorn.wsp.entity.WspRole;
import com.unicorn.wsp.entity.WspUserRole;
import com.unicorn.wsp.entity.vo.WspRoleVO;
import com.unicorn.wsp.mapper.WspRoleMapper;
import com.unicorn.wsp.common.result.Result;
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
public interface WspRoleService extends IService<WspRole>  {





    /**
     * 查询权限list
     * */
    List<WspRole> queryRoleList();




    WspRole getInfoById(Long id);

    boolean save(WspRoleVO dto);

    boolean updateById(WspRoleVO dto);

    boolean removeById(Long id);

    IPage<WspRole> page(WspRoleVO dto);


}

