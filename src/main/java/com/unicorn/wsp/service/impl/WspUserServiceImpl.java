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
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.unicorn.wsp.common.annotation.Excel;
import com.unicorn.wsp.common.result.Result;
import com.unicorn.wsp.entity.WspGoodsPic;
import com.unicorn.wsp.entity.WspUser;
import com.unicorn.wsp.entity.WspUserRole;
import com.unicorn.wsp.entity.exportvo.WspUserETO;
import com.unicorn.wsp.entity.vo.WspUserVO;
import com.unicorn.wsp.entity.zznvo.UserPageVo;
import com.unicorn.wsp.mapper.WspUserMapper;
import com.unicorn.wsp.mapper.WspUserRoleMapper;
import com.unicorn.wsp.service.WspUserRoleService;
import com.unicorn.wsp.service.WspUserService;
import com.unicorn.wsp.utils.DozerUtil;
import com.unicorn.wsp.vo.PageVo;
import com.unicorn.wsp.vo.UserVo;
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
public class WspUserServiceImpl extends ServiceImpl<WspUserMapper,WspUser>
    implements WspUserService {

    @Autowired
    WspUserMapper userMapper;

    @Autowired
    WspUserRoleService userRoleService;



    @Override
    public List<WspUser> getUser(String phone){
        LambdaQueryWrapper<WspUser> queryWrapper = new LambdaQueryWrapper<>();
        if (ObjectUtil.isNotNull(phone)) {
            queryWrapper.eq(WspUser::getUserPhone,phone);
        }
        queryWrapper.ne(WspUser::getIsDisable,"1");
        List<WspUser> wspUsers = userMapper.selectList(queryWrapper);

        return wspUsers;
    }

    @Override
    public WspUser getUserById(String id){
        LambdaQueryWrapper<WspUser> queryWrapper = new LambdaQueryWrapper<>();
        if (ObjectUtil.isNotNull(id)) {
            queryWrapper.eq(WspUser::getUserId,id);
        }
        //queryWrapper.ne(WspUser::getIsDisable,"1");
        WspUser wspUser = userMapper.selectOne(queryWrapper);
        return wspUser;
    }

    // 用户 list
    @Override
    public PageVo<WspUser> queryList(UserPageVo user){
        LambdaQueryWrapper<WspUser> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(ObjectUtil.isNotNull(user.getUserPhone())){
            userLambdaQueryWrapper.like(WspUser::getUserPhone,user.getUserPhone());
        }
        if(ObjectUtil.isNotNull(user.getUserNo())){
            userLambdaQueryWrapper.like(WspUser::getUserNo, user.getUserNo());
        }
        if(ObjectUtil.isNotNull(user.getUserCom())){
            userLambdaQueryWrapper.eq(WspUser::getUserCom, user.getUserCom());
        }
        PageHelper.startPage(user.getPageNum(), user.getPageSize());
        List<WspUser> wspUsers = userMapper.selectList(userLambdaQueryWrapper);
        PageInfo<WspUser> pageInfo = new PageInfo<>(wspUsers);
        PageVo<WspUser> wspUserPageVo = new PageVo<>(pageInfo.getPageNum(), pageInfo.getSize(), wspUsers, pageInfo.getTotal());
        return wspUserPageVo;
    }




    /**
     * 用户list，增加了权限字段
     * */
    public PageVo<UserVo> queryListWithRoleId(UserPageVo user){
        LambdaQueryWrapper<WspUser> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(ObjectUtil.isNotNull(user.getUserPhone())){
            userLambdaQueryWrapper.like(WspUser::getUserPhone,user.getUserPhone());
        }
        if(ObjectUtil.isNotNull(user.getUserNo())){
            userLambdaQueryWrapper.like(WspUser::getUserNo, user.getUserNo());
        }
        if(ObjectUtil.isNotNull(user.getUserCom())){
            userLambdaQueryWrapper.eq(WspUser::getUserCom, user.getUserCom());
        }
        PageHelper.startPage(user.getPageNum(), user.getPageSize());
        List<WspUser> wspUsers = userMapper.selectList(userLambdaQueryWrapper);

        List<UserVo> userVos = new ArrayList<>();
        userVos = DozerUtil.mapList(wspUsers, UserVo.class);

        // 组装roleId(效率应当优化，不过该接口应该基本不会被调用)
        for(UserVo vo:userVos){
            int roleId = userRoleService.queryAdminLevel(vo.getUserId());
            vo.setRoleId(roleId);
        }

        PageInfo<UserVo> pageInfo = new PageInfo<>(userVos);
        PageVo<UserVo> wspUserPageVo = new PageVo<>(pageInfo.getPageNum(), pageInfo.getSize(), userVos, pageInfo.getTotal());
        return wspUserPageVo;
    }




    /**
     * 编辑用户权限
     * */
    public boolean editUserCom(WspUser user){
        LambdaQueryWrapper<WspUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WspUser::getUserId, user.getUserId());
        int update = userMapper.update(WspUser.builder().userCom(user.getUserCom()).build(), wrapper);
        return update>0;
    }


    // 根据手机号查用户
    @Override
    public WspUser queryUserByPhone(String phone){
        LambdaQueryWrapper<WspUser> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(WspUser::getUserPhone, phone);
        WspUser wspUser = userMapper.selectOne(userLambdaQueryWrapper);
        return wspUser;
    }







  //////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public WspUser getInfoById(Long id) {
        return super.getById(id);
    }

    @Override
    public boolean save(WspUserVO dto) {
        WspUser entity = new WspUser();
        BeanUtils.copyProperties(dto, entity);
        return super.save(entity);
    }

    @Override
    public boolean updateById(WspUserVO dto) {
        WspUser entity = new WspUser();
        BeanUtils.copyProperties(dto, entity);
        return super.updateById(entity);
    }

    @Override
    public boolean removeById(Long id) {
        return super.removeById(id);
    }

    @Override
    public IPage<WspUser> page(WspUserVO dto) {
        LambdaQueryWrapper<WspUser> queryWrapper = new LambdaQueryWrapper<>();
        if (ObjectUtil.isNotNull(dto)) {

        }
        Page<WspUser> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        return this.page(page, queryWrapper);
    }
}
