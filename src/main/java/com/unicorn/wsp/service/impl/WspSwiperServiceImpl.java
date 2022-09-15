package com.unicorn.wsp.service.impl;


import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.unicorn.wsp.entity.WspDept;
import com.unicorn.wsp.entity.WspSwiper;
import com.unicorn.wsp.entity.WspSwiper;
import com.unicorn.wsp.entity.vo.WspSwiperVO;
import com.unicorn.wsp.mapper.WspDeptMapper;
import com.unicorn.wsp.mapper.WspSwiperMapper;
import com.unicorn.wsp.service.WspDeptService;
import com.unicorn.wsp.service.WspSwiperService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
* <p>
    *  服务类
    * </p>
*
* @author spark
* @since 1.0
*/
@Service
public class WspSwiperServiceImpl extends ServiceImpl<WspSwiperMapper, WspSwiper>
    implements WspSwiperService {
    @Override
    public WspSwiper getInfoById(String id) {
        return super.getById(id);
    }

    @Override
    public boolean save(WspSwiperVO dto) {
        WspSwiper entity = new WspSwiper();
        BeanUtils.copyProperties(dto, entity);
        return super.save(entity);
    }

    @Override
    public boolean updateById(WspSwiperVO dto) {
        WspSwiper entity = new WspSwiper();
        BeanUtils.copyProperties(dto, entity);
        return super.updateById(entity);
    }

    @Override
    public boolean removeById(String id) {
        return super.removeById(id);
    }

    @Override
    public IPage<WspSwiper> page(WspSwiperVO dto) {
        LambdaQueryWrapper<WspSwiper> queryWrapper = new LambdaQueryWrapper<>();
        if (ObjectUtil.isNotNull(dto)) {

        }
        Page<WspSwiper> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        return this.page(page, queryWrapper);
    }
}
