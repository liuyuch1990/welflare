package com.unicorn.wsp.controller;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.pagehelper.util.StringUtil;
import com.unicorn.wsp.entity.WspCardQuota;
import com.unicorn.wsp.entity.zznvo.WspCardQuotaPageVo;
import com.unicorn.wsp.mapper.WspCardQuotaMapper;
import com.unicorn.wsp.service.WspComService;
import com.unicorn.wsp.utils.TokenUtils;
import com.unicorn.wsp.vo.AccessToken;
import com.unicorn.wsp.vo.GiftCardTypeVo;
import com.unicorn.wsp.vo.ResultVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.unicorn.wsp.common.base.BaseController;
import com.unicorn.wsp.common.consts.SwaggerTagConst;
import com.unicorn.wsp.common.result.Result;
import com.unicorn.wsp.entity.vo.WspCardQuotaVO;
import com.unicorn.wsp.service.WspCardQuotaService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.Service;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@Api(tags = {""})
@RequestMapping("/card/quota")
public class WspCardQuotaController
	extends BaseController<WspCardQuotaService, WspCardQuotaVO> {

    @Autowired
    WspCardQuotaService quotaService;

    @Autowired
    WspCardQuotaMapper quotaMapper;


    @Autowired
    WspComService comService;





//    /**
//     * 迭代 查用户公司对应商品种类 下拉
//     * */
//    @GetMapping("/queryGoodsType")
//    public ResultVo queryGoodsType(HttpServletRequest request){
//
//        AccessToken token = TokenUtils.analyseRequest(request);
//
//        List<String> list = null;
//        if(StringUtils.isNotEmpty(token.getComNum())){
//            list = comService.queryTypeListByCom(token.getComNum());
//        }
//        return ResultVo.success(list);
//    }








    // 礼品卡种类详情
//    @GetMapping("/queryById/{id}")
//    public ResultVo queryById(@PathVariable int id){
//        return ResultVo.success(quotaService.queryById(id));
//    }
//
















    ///////////////////////////////////////////////////////////////////////////////

    @PostMapping("/add")
    @ApiOperation(value = "添加", notes = "添加")
    @Override
    protected Result add(@RequestBody @Validated WspCardQuotaVO dto) {
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
    protected Result edit(@RequestBody @Validated WspCardQuotaVO dto) {
        service.updateById(dto);
        return new Result().success("修改成功！");
    }

    @ApiOperation(value = "查询分页", notes = "查询分页")
    @PostMapping("/page")
    @Override
    protected Result page(@RequestBody @Validated WspCardQuotaVO dto) {
        return new Result().success(service.page(dto));
    }


}