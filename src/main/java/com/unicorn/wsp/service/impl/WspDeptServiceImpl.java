package com.unicorn.wsp.service.impl;


import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.unicorn.wsp.entity.WspCom;
import com.unicorn.wsp.entity.WspDept;
import com.unicorn.wsp.entity.vo.WspDeptVO;
import com.unicorn.wsp.mapper.WspDeptMapper;
import com.unicorn.wsp.service.SimpleDictService;
import com.unicorn.wsp.service.WspDeptService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
public class WspDeptServiceImpl extends ServiceImpl<WspDeptMapper,WspDept>
    implements WspDeptService {

}
