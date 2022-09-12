package com.unicorn.wsp.controller;


import cn.hutool.core.util.ObjectUtil;
import com.unicorn.wsp.common.exception.BusinessRollbackException;
import com.unicorn.wsp.common.result.Result;
import com.unicorn.wsp.entity.WspCom;
import com.unicorn.wsp.entity.WspRole;
import com.unicorn.wsp.entity.WspUser;
import com.unicorn.wsp.entity.WspUserRole;
import com.unicorn.wsp.entity.zznvo.UserPageVo;
import com.unicorn.wsp.service.WspComService;
import com.unicorn.wsp.service.WspRoleService;
import com.unicorn.wsp.service.WspUserRoleService;
import com.unicorn.wsp.service.WspUserService;
import com.unicorn.wsp.utils.ObjectFieldUtils;
import com.unicorn.wsp.vo.PageVo;
import com.unicorn.wsp.vo.ResultVo;
import com.unicorn.wsp.vo.UserVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/administrator")
public class AdministratorController {


    @Autowired
    WspUserRoleService userRoleService;

    @Autowired
    WspRoleService roleService;

    @Autowired
    WspUserService userService;

    @Autowired
    WspComService comService;


    /**
     * 查看权限表
     */
    @GetMapping("/queryRole")
    public ResultVo queryRoleList(){
        List<WspRole> wspUserRoles = roleService.queryRoleList();
        return ResultVo.success(wspUserRoles);
    }


    /**
     * 查看人员权限
     * */
    @PostMapping("/queryUserRoleList")
    public ResultVo queryUserRoleList(@RequestBody UserPageVo vo){
        PageVo<UserVo> userVoPageVo = userService.queryListWithRoleId(vo);
        return ResultVo.success(userVoPageVo);
    }


    /**
     * 编辑人员角色
     * */
    @PostMapping("/editUserRole")
    public ResultVo editUserRole(@RequestBody WspUserRole userRole){
        userRole.setId(null);
        boolean b = userRoleService.editUserRole(userRole);
        return b ? ResultVo.success(): ResultVo.failed();
    }


    /**
     * 编辑人员公司
     * */
    @PostMapping("/editUserCom")
    public ResultVo editUserCom(@RequestBody WspUser user){
        boolean b = ObjectFieldUtils.isExistSpecifiedFieldIsEmpty(user, "userId", "userCom");
        if(b) return ResultVo.argsError("用户id和公司均不能为空");
        boolean editRes = userService.editUserCom(user);
        return editRes ? ResultVo.success(): ResultVo.failed();
    }

    /**
     * 编辑人员角色和公司
     * */
    @Transactional
    @PostMapping("/editUserRoleAndCom")
    public ResultVo editUserComAndCom(@RequestBody  UserVo vo){
        if(StringUtils.isEmpty(vo.getUserId()))
            return ResultVo.argsError("用户id不能为空");

        boolean editCom = false;
        boolean editRole = false;
        if(StringUtils.isNotEmpty(vo.getUserCom())){
            editCom = userService.editUserCom(vo);
        }
        if(ObjectUtil.isNotEmpty(vo.getRoleId())){
            editRole = userRoleService.editUserRole(WspUserRole.builder()
                    .userId(vo.getUserId())
                    .roleId(vo.getRoleId())
                    .build());
        }
        if(! editCom && ! editRole){
            throw new BusinessRollbackException("操作失败");
        }
        return ResultVo.success();

    }


    /**
     * 删除公司
     * */
    @DeleteMapping("/dropCom/{comNum}")
    public ResultVo dropCom(@PathVariable String comNum){
        boolean b = comService.dropCom(comNum);
        return b ? ResultVo.success(): ResultVo.failed();
    }


    /**
     * 查看公司list
     * */
    @GetMapping("/queryComList")
    public ResultVo queryComList(){
        List<WspCom> wspComs = comService.queryComNumList();
        return ResultVo.success(wspComs);
    }




}


