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
import com.unicorn.wsp.common.exception.BusinessRollbackException;
import com.unicorn.wsp.common.result.Result;
import com.unicorn.wsp.entity.WspIndexPage;
import com.unicorn.wsp.entity.exportvo.WspIndexPageETO;
import com.unicorn.wsp.entity.vo.WspIndexPageVO;
import com.unicorn.wsp.mapper.WspIndexPageMapper;
import com.unicorn.wsp.service.WspIndexPageService;
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
public class WspIndexPageServiceImpl extends ServiceImpl<WspIndexPageMapper,WspIndexPage>
    implements WspIndexPageService {

    @Autowired
    WspIndexPageMapper pageMapper;


    /**
     * 迭代：公司唯一
     * */
    public boolean isExit(String com){
        LambdaQueryWrapper<WspIndexPage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WspIndexPage::getComNum, com);
        Integer count = pageMapper.selectCount(wrapper);
        return count>0;
    }

    /**
     * 迭代：查询
     * */
    public WspIndexPage queryIndexPageInfoByCom(String comNum){
        LambdaQueryWrapper<WspIndexPage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WspIndexPage::getComNum, comNum);
        WspIndexPage wspIndexPages = new WspIndexPage();
        try{
            wspIndexPages = pageMapper.selectOne(wrapper);
        }catch (Exception e){
            return wspIndexPages;
        }
        return wspIndexPages;
    }


    /**
     * 迭代：编辑
     * */
    @Transactional
    public boolean editIndexPageInfo(WspIndexPage vo){
        LambdaQueryWrapper<WspIndexPage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WspIndexPage::getComNum, vo.getComNum());
        int update = pageMapper.update(vo, wrapper);
        if(update!=1) throw new BusinessRollbackException("公司首页信息异常");
        return true;
    }










    @Override
    public WspIndexPage getInfoById(Long id) {
        return super.getById(id);
    }

    @Override
    public boolean save(WspIndexPageVO dto) {
        WspIndexPage entity = new WspIndexPage();
        BeanUtils.copyProperties(dto, entity);
        return super.save(entity);
    }

    @Override
    public boolean updateById(WspIndexPageVO dto) {
        WspIndexPage entity = new WspIndexPage();
        BeanUtils.copyProperties(dto, entity);
        return super.updateById(entity);
    }

    @Override
    public boolean removeById(Long id) {
        return super.removeById(id);
    }

    @Override
    public IPage<WspIndexPage> page(WspIndexPageVO dto) {
        LambdaQueryWrapper<WspIndexPage> queryWrapper = new LambdaQueryWrapper<>();
        if (ObjectUtil.isNotNull(dto)) {

        }
        Page<WspIndexPage> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        return this.page(page, queryWrapper);
    }
}
