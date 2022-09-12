package com.unicorn.wsp.service.impl;



import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.unicorn.wsp.common.annotation.Excel;
import com.unicorn.wsp.common.result.Result;
import com.unicorn.wsp.entity.SimpleDict;
import com.unicorn.wsp.entity.WspCom;
import com.unicorn.wsp.entity.exportvo.SimpleDictETO;
import com.unicorn.wsp.entity.vo.SimpleDictVO;
import com.unicorn.wsp.mapper.SimpleDictMapper;
import com.unicorn.wsp.mapper.WspComMapper;
import com.unicorn.wsp.service.SimpleDictService;
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
import java.util.Arrays;
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
public class SimpleDictServiceImpl extends ServiceImpl<SimpleDictMapper,SimpleDict>
    implements SimpleDictService {

    @Autowired
    SimpleDictMapper mapper;

    public List<SimpleDict> queryDict(String dictType){
        LambdaQueryWrapper<SimpleDict> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SimpleDict::getDictType, dictType);
        List<SimpleDict> simpleDicts = mapper.selectList(wrapper);
        return simpleDicts;
    }








    ////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public SimpleDict getInfoById(Long id) {
        return super.getById(id);
    }

    @Override
    public boolean save(SimpleDictVO dto) {
        SimpleDict entity = new SimpleDict();
        BeanUtils.copyProperties(dto, entity);
        return super.save(entity);
    }

    @Override
    public boolean updateById(SimpleDictVO dto) {
        SimpleDict entity = new SimpleDict();
        BeanUtils.copyProperties(dto, entity);
        return super.updateById(entity);
    }

    @Override
    public boolean removeById(Long id) {
        return super.removeById(id);
    }

    @Override
    public IPage<SimpleDict> page(SimpleDictVO dto) {
        LambdaQueryWrapper<SimpleDict> queryWrapper = new LambdaQueryWrapper<>();
        if (ObjectUtil.isNotNull(dto)) {

        }
        Page<SimpleDict> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        return this.page(page, queryWrapper);
    }
}
