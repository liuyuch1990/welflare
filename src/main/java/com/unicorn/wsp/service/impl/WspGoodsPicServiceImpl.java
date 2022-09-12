package com.unicorn.wsp.service.impl;



import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.unicorn.wsp.common.annotation.Excel;
import com.unicorn.wsp.common.result.Result;
import com.unicorn.wsp.entity.WspGoodsPic;
import com.unicorn.wsp.entity.exportvo.WspGoodsPicETO;
import com.unicorn.wsp.entity.vo.WspGoodsPicVO;
import com.unicorn.wsp.mapper.WspGoodsPicMapper;
import com.unicorn.wsp.service.WspGoodsPicService;
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
public class WspGoodsPicServiceImpl extends ServiceImpl<WspGoodsPicMapper,WspGoodsPic>
    implements WspGoodsPicService {

    @Autowired
    WspGoodsPicMapper picMapper;

    // 根據商品id查圖片list
    public List<WspGoodsPic> queryListByGoodId(String id){

        LambdaQueryWrapper<WspGoodsPic> queryWrapper = new LambdaQueryWrapper<>();
        if (ObjectUtil.isNotNull(id)) {
            queryWrapper.eq(WspGoodsPic::getGoodsId,id);
            queryWrapper.orderByAsc(WspGoodsPic::getPicSort);
        }
        List<WspGoodsPic> wspGoodsPics = picMapper.selectList(queryWrapper);
        return wspGoodsPics;

    }
    // 查商品封面
    public String queryCoverByGoodId(String id){
        LambdaQueryWrapper<WspGoodsPic> queryWrapper = new LambdaQueryWrapper<>();
        if (ObjectUtil.isNotNull(id)) {
            queryWrapper.eq(WspGoodsPic::getGoodsId,id);
            queryWrapper.eq(WspGoodsPic::getIsCover,1);
        }
        List<WspGoodsPic> wspGoodsPics = picMapper.selectList(queryWrapper);
        if(!ObjectUtil.isEmpty(wspGoodsPics)){
            return wspGoodsPics.get(0).getPicSavepath();
        }
        return "No cover" + ": " + id;
    }

    @Override
    public WspGoodsPic getInfoById(Long id) {
        return super.getById(id);
    }

    @Override
    public boolean save(WspGoodsPicVO dto) {
        WspGoodsPic entity = new WspGoodsPic();
        BeanUtils.copyProperties(dto, entity);
        return super.save(entity);
    }

    @Override
    public boolean updateById(WspGoodsPicVO dto) {
        WspGoodsPic entity = new WspGoodsPic();
        BeanUtils.copyProperties(dto, entity);
        return super.updateById(entity);
    }

    @Override
    public boolean removeById(Long id) {
        return super.removeById(id);
    }

    @Override
    public IPage<WspGoodsPic> page(WspGoodsPicVO dto) {
        LambdaQueryWrapper<WspGoodsPic> queryWrapper = new LambdaQueryWrapper<>();
        if (ObjectUtil.isNotNull(dto)) {

        }
        Page<WspGoodsPic> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        return this.page(page, queryWrapper);
    }
}
