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
import com.unicorn.wsp.entity.DemandConfig;
import com.unicorn.wsp.entity.exportvo.DemandConfigETO;
import com.unicorn.wsp.entity.vo.DemandConfigVO;
import com.unicorn.wsp.mapper.DemandConfigMapper;
import com.unicorn.wsp.service.DemandConfigService;
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
public class DemandConfigServiceImpl extends ServiceImpl<DemandConfigMapper,DemandConfig>
    implements DemandConfigService {

    @Autowired
    DemandConfigMapper demandConfigMapper;

    @Override
    public DemandConfig getInfoById(Long id) {
        return super.getById(id);
    }

    @Override
    public boolean save(DemandConfigVO dto) {
        DemandConfig entity = new DemandConfig();
        BeanUtils.copyProperties(dto, entity);
        return super.save(entity);
    }

    @Override
    public boolean updateById(DemandConfigVO dto) {
        DemandConfig entity = new DemandConfig();
        BeanUtils.copyProperties(dto, entity);
        return super.updateById(entity);
    }

    @Override
    public boolean removeById(Long id) {
        return super.removeById(id);
    }

    @Override
    public IPage<DemandConfig> page(DemandConfigVO dto) {
        LambdaQueryWrapper<DemandConfig> queryWrapper = new LambdaQueryWrapper<>();
        if (ObjectUtil.isNotNull(dto)) {

        }
        Page<DemandConfig> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        return this.page(page, queryWrapper);
    }

    // 21.12.15判断需求是否启用
    @Override
    public boolean checkDemand(int id, int configType){
        LambdaQueryWrapper<DemandConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DemandConfig::getId, id);
        wrapper.eq(DemandConfig::getConfigType, configType);

        Integer res = demandConfigMapper.selectCount(wrapper);

        if(res == 0){
            return false;
        }
        return true;
    }

    // 21.12.28判断需求是否启用
    @Override
    public int checkDemand(int id){
        LambdaQueryWrapper<DemandConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DemandConfig::getId, id);

        DemandConfig demandConfig = demandConfigMapper.selectOne(wrapper);

        return demandConfig.getConfigType();
    }


}
