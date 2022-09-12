package com.unicorn.wsp.controller;

import com.unicorn.wsp.entity.WspIndexPage;
import com.unicorn.wsp.utils.TokenUtils;
import com.unicorn.wsp.utils.ValidationUtil;
import com.unicorn.wsp.vo.AccessToken;
import com.unicorn.wsp.vo.ResultVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.unicorn.wsp.common.base.BaseController;
import com.unicorn.wsp.common.consts.SwaggerTagConst;
import com.unicorn.wsp.common.result.Result;
import com.unicorn.wsp.entity.vo.WspIndexPageVO;
import com.unicorn.wsp.service.WspIndexPageService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@Api(tags = {""})
@RequestMapping("/index/page")
public class WspIndexPageController
	extends BaseController<WspIndexPageService, WspIndexPageVO> {


    /**
     * 迭代：新增
     * */
    @PostMapping("/addIndexInfo")
    public ResultVo addIndexInfo(@RequestBody WspIndexPage vo, HttpServletRequest request){
        ValidationUtil.validateObject(vo);

        AccessToken token = TokenUtils.analyseRequest(request);
        String comNum = token.getComNum();
        boolean exit = service.isExit(comNum);
        if(exit) return ResultVo.failed("该公司已存在首页信息，请删除再尝试添加");
        vo.setComNum(comNum);
        boolean save = service.save(vo);
        return save? ResultVo.success(): ResultVo.failed();
    }


    /**
     * 迭代：查询
     * */
    @GetMapping("/queryIndexPageInfo")
    public ResultVo queryIndexPageInfo(HttpServletRequest request){
        AccessToken accessToken = TokenUtils.analyseRequest(request);
        WspIndexPage wspIndexPage = service.queryIndexPageInfoByCom(accessToken.getComNum());
        return ResultVo.success(wspIndexPage);
    }

    /**
     * 迭代：编辑
     * */
    @PostMapping("/editIndexPageInfo")
    public ResultVo editIndexPageInfo(@RequestBody WspIndexPage vo, HttpServletRequest request){
        ValidationUtil.validateObject(vo);
        AccessToken accessToken = TokenUtils.analyseRequest(request);
        vo.setComNum(accessToken.getComNum());
        boolean b = service.editIndexPageInfo(vo);
        return b ? ResultVo.success(): ResultVo.failed();
    }






    @PostMapping("/add")
    @ApiOperation(value = "添加", notes = "添加")
    @Override
    protected Result add(@RequestBody @Validated WspIndexPageVO dto) {
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
    protected Result edit(@RequestBody @Validated WspIndexPageVO dto) {
        service.updateById(dto);
        return new Result().success("修改成功！");
    }

    @ApiOperation(value = "查询分页", notes = "查询分页")
    @PostMapping("/page")
    @Override
    protected Result page(@RequestBody @Validated WspIndexPageVO dto) {
        return new Result().success(service.page(dto));
    }


}