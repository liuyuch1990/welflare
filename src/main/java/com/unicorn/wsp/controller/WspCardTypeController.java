package com.unicorn.wsp.controller;

import com.unicorn.wsp.entity.WspCardType;
import com.unicorn.wsp.entity.zznvo.WspCardQuotaPageVo;
import com.unicorn.wsp.utils.TokenUtils;
import com.unicorn.wsp.vo.AccessToken;
import com.unicorn.wsp.vo.GiftCardTypeVo;
import com.unicorn.wsp.vo.PageVo;
import com.unicorn.wsp.vo.ResultVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.unicorn.wsp.common.base.BaseController;
import com.unicorn.wsp.common.result.Result;
import com.unicorn.wsp.entity.vo.WspCardTypeVO;
import com.unicorn.wsp.service.WspCardTypeService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Api(tags = {""})
@RequestMapping("/card/type")
public class WspCardTypeController
	extends BaseController<WspCardTypeService, WspCardTypeVO> {



    /**
     * 迭代：类品卡种类列表
     * */
    @PostMapping("/queryGiftCardList")
    public ResultVo queryGiftCard(@RequestBody WspCardQuotaPageVo vo, HttpServletRequest request){
        AccessToken accessToken = TokenUtils.analyseRequest(request);
        int roleId = accessToken.getRoleId();
        if(roleId == 2){
            vo.setComNum(accessToken.getComNum());
        }
        PageVo<Map<String, Object>> pageVo = service.queryCardTypeList(vo);
        return ResultVo.success(pageVo);
    }

    /**
     * 迭代：导入礼品卡时额度下拉
     * */
    @GetMapping("/queryCardType")
    public ResultVo queryCardType(HttpServletRequest request) {
        AccessToken accessToken = TokenUtils.analyseRequest(request);
        List<WspCardType> list = service.queryCardTypeDropDownList(accessToken.getComNum());
        return ResultVo.success(list);
    }


    /**
     * 迭代：编辑礼品卡额度
     * */
    @PostMapping("/updateQuota")
    public ResultVo updateQuota(@RequestBody GiftCardTypeVo card){
        boolean b1 = service.onlyNameExcludeOldName(card.getCardTypeName(), card.getCardTypeId());
        if(b1) return ResultVo.failed("礼品卡名称重复");
        boolean b = service.updateQuota(card);
        return b?ResultVo.success():ResultVo.failed();
    }

    /**
     * 迭代：删除礼品卡种类
     * */
    @GetMapping("/deleteCardType/{cardTypeId}")
    public ResultVo deleteCard(@PathVariable String cardTypeId){
        boolean b = service.deleteCardType(cardTypeId);
        return b?ResultVo.success():ResultVo.failed();
    }

    /**
     * 迭代：新建礼品卡种类
     * */
    @PostMapping("/createCard")
    public ResultVo createCard(@RequestBody GiftCardTypeVo vo, HttpServletRequest request){

        boolean b = service.onlyName(vo.getCardTypeName());
        if(b) return ResultVo.failed("礼品卡名称重复");

        AccessToken token = TokenUtils.analyseRequest(request);
        String comNum = token.getComNum();
        vo.setComNum(comNum);
        service.createCard(vo);

        return ResultVo.success();
    }

    /**
     * 迭代：礼品卡种类详情
     * */
    @GetMapping("/detail/{cardTypeId}")
    public ResultVo editCard(@PathVariable String cardTypeId){
        HashMap<String, Object> map = service.queryCardTypeById(cardTypeId);
        return ResultVo.success(map);
    }





























    @PostMapping("/add")
    @ApiOperation(value = "添加", notes = "添加")
    @Override
    protected Result add(@RequestBody @Validated WspCardTypeVO dto) {
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
    protected Result edit(@RequestBody @Validated WspCardTypeVO dto) {
        service.updateById(dto);
        return new Result().success("修改成功！");
    }

    @ApiOperation(value = "查询分页", notes = "查询分页")
    @PostMapping("/page")
    @Override
    protected Result page(@RequestBody @Validated WspCardTypeVO dto) {
        return new Result().success(service.page(dto));
    }


}