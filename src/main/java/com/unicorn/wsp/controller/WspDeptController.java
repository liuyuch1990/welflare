package com.unicorn.wsp.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.unicorn.wsp.common.base.BaseController;
import com.unicorn.wsp.common.result.Result;
import com.unicorn.wsp.entity.WspDept;
import com.unicorn.wsp.service.SimpleDictService;
import com.unicorn.wsp.service.WspComService;
import com.unicorn.wsp.service.WspDeptService;
import com.unicorn.wsp.vo.ResultVo;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@Api(tags = {""})
@RequestMapping("/dept")
public class WspDeptController
	extends BaseController<WspDeptService, WspDept> {

    @Autowired
    SimpleDictService dictService;
    

    // 部门下拉
    @GetMapping("/queryDeptList")
    public ResultVo queryComNum(HttpServletRequest request){
        List<WspDept> wspDepts = service.list(new QueryWrapper<>());
        return ResultVo.success(wspDepts);
    }


    @Override
    protected Result add(WspDept dto) {
        return null;
    }

    @Override
    protected Result delete(Long id) {
        return null;
    }

    @Override
    protected Result get(Long id) {
        return null;
    }

    @Override
    protected Result edit(WspDept dto) {
        return null;
    }

    @Override
    protected Result page(WspDept dto) {
        return null;
    }
}