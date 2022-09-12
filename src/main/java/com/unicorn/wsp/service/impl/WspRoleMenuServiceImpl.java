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
import com.unicorn.wsp.entity.WspRoleMenu;
import com.unicorn.wsp.entity.exportvo.WspRoleMenuETO;
import com.unicorn.wsp.entity.vo.WspRoleMenuVO;
import com.unicorn.wsp.mapper.WspRoleMenuMapper;
import com.unicorn.wsp.service.WspRoleMenuService;
import org.springframework.beans.BeanUtils;
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
public class WspRoleMenuServiceImpl extends ServiceImpl<WspRoleMenuMapper,WspRoleMenu>
    implements WspRoleMenuService {


    @Override
    public WspRoleMenu getInfoById(Long id) {
        return super.getById(id);
    }

    @Override
    public boolean save(WspRoleMenuVO dto) {
        WspRoleMenu entity = new WspRoleMenu();
        BeanUtils.copyProperties(dto, entity);
        return super.save(entity);
    }

    @Override
    public boolean updateById(WspRoleMenuVO dto) {
        WspRoleMenu entity = new WspRoleMenu();
        BeanUtils.copyProperties(dto, entity);
        return super.updateById(entity);
    }

    @Override
    public boolean removeById(Long id) {
        return super.removeById(id);
    }

    @Override
    public IPage<WspRoleMenu> page(WspRoleMenuVO dto) {
        LambdaQueryWrapper<WspRoleMenu> queryWrapper = new LambdaQueryWrapper<>();
        if (ObjectUtil.isNotNull(dto)) {

        }
        Page<WspRoleMenu> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        return this.page(page, queryWrapper);
    }
}
