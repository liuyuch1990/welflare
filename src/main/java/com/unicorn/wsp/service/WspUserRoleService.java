package com.unicorn.wsp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.unicorn.wsp.entity.WspUserRole;
import com.unicorn.wsp.entity.vo.WspUserRoleVO;
import com.unicorn.wsp.mapper.WspUserRoleMapper;
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
public interface WspUserRoleService extends IService<WspUserRole>  {

    WspUserRole getInfoById(Long id);

    boolean save(WspUserRoleVO dto);

    boolean updateById(WspUserRoleVO dto);

    boolean removeById(Long id);

    IPage<WspUserRole> page(WspUserRoleVO dto);

    // 校验管理员角色
    int checkAdmin(String userId);

    // 查询用户权限(1普通用户2管理员3超级管理员
    int queryAdminLevel(String userId);

    /**
     * 编辑人员权限
     * */
    boolean editUserRole(WspUserRole wspUserRole);


}

