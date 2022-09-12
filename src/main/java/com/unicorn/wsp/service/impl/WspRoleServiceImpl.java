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
import com.unicorn.wsp.entity.WspRole;
import com.unicorn.wsp.entity.WspUserRole;
import com.unicorn.wsp.entity.exportvo.WspRoleETO;
import com.unicorn.wsp.entity.vo.WspRoleVO;
import com.unicorn.wsp.mapper.WspRoleMapper;
import com.unicorn.wsp.service.WspRoleService;
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
public class WspRoleServiceImpl extends ServiceImpl<WspRoleMapper,WspRole>
    implements WspRoleService {


    @Autowired
    WspRoleMapper mapper;


    /**
     * 查询权限list
     * */
    public List<WspRole> queryRoleList(){
        LambdaQueryWrapper<WspRole> wrapper = new LambdaQueryWrapper<>();
        List<WspRole> wspUserRoles = mapper.selectList(wrapper);
        return wspUserRoles;
    }



    @Override
    public WspRole getInfoById(Long id) {
        return super.getById(id);
    }

    @Override
    public boolean save(WspRoleVO dto) {
        WspRole entity = new WspRole();
        BeanUtils.copyProperties(dto, entity);
        return super.save(entity);
    }

    @Override
    public boolean updateById(WspRoleVO dto) {
        WspRole entity = new WspRole();
        BeanUtils.copyProperties(dto, entity);
        return super.updateById(entity);
    }

    @Override
    public boolean removeById(Long id) {
        return super.removeById(id);
    }

    @Override
    public IPage<WspRole> page(WspRoleVO dto) {
        LambdaQueryWrapper<WspRole> queryWrapper = new LambdaQueryWrapper<>();
        if (ObjectUtil.isNotNull(dto)) {

        }
        Page<WspRole> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        return this.page(page, queryWrapper);
    }
}
