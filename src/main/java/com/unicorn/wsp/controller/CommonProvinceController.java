package com.unicorn.wsp.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.unicorn.wsp.common.base.BaseController;
import com.unicorn.wsp.common.consts.SwaggerTagConst;
import com.unicorn.wsp.common.result.Result;
import com.unicorn.wsp.entity.vo.CommonProvinceVO;
import com.unicorn.wsp.service.CommonProvinceService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@Api(tags = {""})
@RequestMapping("/province")
public class CommonProvinceController
	extends BaseController<CommonProvinceService, CommonProvinceVO> {


    @PostMapping("/add")
    @ApiOperation(value = "添加", notes = "添加")
    @Override
    protected Result add(@RequestBody @Validated CommonProvinceVO dto) {
        service.save(dto);
        return new Result().success("创建/修改成功！");
    }

    @DeleteMapping("/delete/{id}")
    @ApiOperation(value = "删除", notes = "删除")
    @Override
    protected Result delete(@PathVariable Long id) {
        service.removeById(id);
        return new Result().success("删除成功！");
    }

    @GetMapping("/get/{id}")
    @ApiOperation(value = "获取单个", notes = "获取单个")
    @Override
    protected Result get(@PathVariable Long id) {
        return new Result().success(service.getInfoById(id));
    }

    @PutMapping("/edit")
    @ApiOperation(value = "编辑", notes = "编辑")
    @Override
    protected Result edit(@RequestBody @Validated CommonProvinceVO dto) {
        service.updateById(dto);
        return new Result().success("修改成功！");
    }

    @ApiOperation(value = "查询分页", notes = "查询分页")
    @PostMapping("/page")
    @Override
    protected Result page(@RequestBody @Validated CommonProvinceVO dto) {
        return new Result().success(service.page(dto));
    }


}