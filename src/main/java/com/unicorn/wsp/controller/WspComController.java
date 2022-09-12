package com.unicorn.wsp.controller;

import com.unicorn.wsp.entity.SimpleDict;
import com.unicorn.wsp.service.SimpleDictService;
import com.unicorn.wsp.utils.TokenUtils;
import com.unicorn.wsp.vo.AccessToken;
import com.unicorn.wsp.vo.ResultVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.unicorn.wsp.common.base.BaseController;
import com.unicorn.wsp.common.result.Result;
import com.unicorn.wsp.entity.vo.WspComVO;
import com.unicorn.wsp.service.WspComService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@Api(tags = {""})
@RequestMapping("/com")
public class WspComController
	extends BaseController<WspComService, WspComVO> {

    @Autowired
    SimpleDictService dictService;


    // 校验公司编号是否存在
    @GetMapping("/check/{comNum}")
    public ResultVo checkCom(@PathVariable String comNum){
        String comNumTrim = comNum.trim();
        int i = service.checkCom(comNumTrim);
        return i>0? ResultVo.success():ResultVo.failed();
    }

    // 公司编号下拉
    @GetMapping("/queryComNum")
    public ResultVo queryComNum(HttpServletRequest request){
        List<String> strings = service.queryComNumDropDownList(request);
        return ResultVo.success(strings);
    }

    /**
     * 迭代：得到公司的商品类型下拉
     * */
    @GetMapping("/queryGoodsTypeDropDownList")
    public ResultVo queryDropDownList(HttpServletRequest request){
        AccessToken accessToken = TokenUtils.analyseRequest(request);
        List<Map<String, String>> maps = service.queryTypeMapByCom(accessToken.getComNum());
        return ResultVo.success(maps);
    }

    /**
     * 迭代：添加新的商品种类
     * */
    @PostMapping("/addGoodsType")
    public ResultVo addGoodsType(@RequestBody @Validated SimpleDict dict, HttpServletRequest request){
        //ValidationUtil.validateObject(dict);
        AccessToken accessToken = TokenUtils.analyseRequest(request);
        int i = service.addGoodsType(dict, accessToken.getComNum());
        switch (i){
            case 1: return ResultVo.success("添加成功");
            case 2: return ResultVo.failed("类别标识码重复");
            case 3: return ResultVo.failed("商品种类创建失败");
            default: return ResultVo.failed("绑定失败");
        }
    }

    /**
     * 迭代：绑定公司的商品类型
     * */
    @GetMapping("/addGoodsType/{goodsType}")
    public ResultVo addGoodsType(@PathVariable String goodsType, HttpServletRequest request){
        AccessToken accessToken = TokenUtils.analyseRequest(request);
        int res = service.addGoodsTypeByCom(goodsType, accessToken.getComNum());
        switch (res){
            case 2: return ResultVo.failed("不存在该类别");
            case 0: return ResultVo.failed();
            default: return ResultVo.success();
        }
    }

    /**
     * 迭代：查询全部公司商品种类下拉（配置公司的商品类型）
     * */
    @GetMapping("/queryTypeAll")
    public ResultVo queryTypeAll(HttpServletRequest request){
        List<SimpleDict> goodsType = dictService.queryDict("goodsType");
        return ResultVo.success(goodsType);
    }



    /**
     * 迭代：删除公司的商品类型
     * */
    @GetMapping("/deleteGoodsType/{goodsType}")
    public ResultVo deleteGoodsType(@PathVariable String goodsType, HttpServletRequest request){
        AccessToken token = TokenUtils.analyseRequest(request);
        boolean b = service.deleteGoodsType(goodsType, token.getComNum());
        return b?ResultVo.success():ResultVo.failed();
    }




























    /////////////////////////////////////////////////////////////////////////////////////////////////

    @PostMapping("/add")
    @ApiOperation(value = "添加", notes = "添加")
    @Override
    protected Result add(@RequestBody @Validated WspComVO dto) {
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
    protected Result edit(@RequestBody @Validated WspComVO dto) {
        service.updateById(dto);
        return new Result().success("修改成功！");
    }

    @ApiOperation(value = "查询分页", notes = "查询分页")
    @PostMapping("/page")
    @Override
    protected Result page(@RequestBody @Validated WspComVO dto) {
        return new Result().success(service.page(dto));
    }


}