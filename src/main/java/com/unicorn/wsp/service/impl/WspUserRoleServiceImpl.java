package com.unicorn.wsp.service.impl;



import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.unicorn.wsp.common.annotation.Excel;
import com.unicorn.wsp.common.result.Result;
import com.unicorn.wsp.entity.WspUserRole;
import com.unicorn.wsp.entity.exportvo.WspUserRoleETO;
import com.unicorn.wsp.entity.vo.WspUserRoleVO;
import com.unicorn.wsp.mapper.WspUserRoleMapper;
import com.unicorn.wsp.service.WspUserRoleService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
/**
* <p>
    *  服务类
    * </p>
*
* @author spark
* @since 1.0
*/
@Service
public class WspUserRoleServiceImpl extends ServiceImpl<WspUserRoleMapper,WspUserRole>
    implements WspUserRoleService {


    @Autowired
    WspUserRoleMapper userRoleMapper;

    // 校验管理员角色
    public int checkAdmin(String userId){
        LambdaQueryWrapper<WspUserRole> userRoleLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userRoleLambdaQueryWrapper.eq(WspUserRole::getUserId,userId);
        userRoleLambdaQueryWrapper.ne(WspUserRole::getRoleId,"1");// 不为普通用户
        int count = userRoleMapper.selectCount(userRoleLambdaQueryWrapper);
        return count;
    }


    // 查询用户权限(1普通用户2管理员3超级管理员
    public int queryAdminLevel(String userId){
        Integer s = userRoleMapper.queryAdminLevel(userId);
        return s;
    }

    /**
     * 编辑人员权限
     * */
    @Override
    public boolean editUserRole(WspUserRole wspUserRole) {
        LambdaQueryWrapper<WspUserRole> userRoleLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userRoleLambdaQueryWrapper.eq(WspUserRole::getUserId,wspUserRole.getUserId());
        int update = userRoleMapper.update(wspUserRole, userRoleLambdaQueryWrapper);
        return update>0;
    }


    @Override
    public WspUserRole getInfoById(Long id) {
        return super.getById(id);
    }

    @Override
    public boolean save(WspUserRoleVO dto) {
        WspUserRole entity = new WspUserRole();
        BeanUtils.copyProperties(dto, entity);
        return super.save(entity);
    }

    @Override
    public boolean updateById(WspUserRoleVO dto) {
        WspUserRole entity = new WspUserRole();
        BeanUtils.copyProperties(dto, entity);
        return super.updateById(entity);
    }

    @Override
    public boolean removeById(Long id) {
        return super.removeById(id);
    }

    @Override
    public IPage<WspUserRole> page(WspUserRoleVO dto) {
        LambdaQueryWrapper<WspUserRole> queryWrapper = new LambdaQueryWrapper<>();
        if (ObjectUtil.isNotNull(dto)) {

        }
        Page<WspUserRole> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        return this.page(page, queryWrapper);
    }
}
