package com.unicorn.wsp.controller;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.unicorn.wsp.entity.CommonArea;
import com.unicorn.wsp.entity.CommonCity;
import com.unicorn.wsp.entity.CommonProvince;
import com.unicorn.wsp.entity.WspAddr;
import com.unicorn.wsp.mapper.CommonAreaMapper;
import com.unicorn.wsp.mapper.CommonCityMapper;
import com.unicorn.wsp.mapper.CommonProvinceMapper;
import com.unicorn.wsp.mapper.WspAddrMapper;
import com.unicorn.wsp.utils.EncryptorUtil;
import com.unicorn.wsp.vo.AccessToken;
import com.unicorn.wsp.vo.ResultVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.unicorn.wsp.common.base.BaseController;
import com.unicorn.wsp.common.consts.SwaggerTagConst;
import com.unicorn.wsp.common.result.Result;
import com.unicorn.wsp.entity.vo.WspAddrVO;
import com.unicorn.wsp.service.WspAddrService;
import lombok.extern.slf4j.Slf4j;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

@RestController
@Api(tags = {""})
@RequestMapping("/addr")
@Slf4j
public class WspAddrController
	extends BaseController<WspAddrService, WspAddrVO> {

    @Autowired
    CommonProvinceMapper provinceMapper;

    @Autowired
    CommonCityMapper cityMapper;

    @Autowired
    CommonAreaMapper areaMapper;

    @Autowired
    WspAddrMapper addrMapper;

    // 省
    @GetMapping("/province")
    public ResultVo queryProvince(){
        LambdaQueryWrapper<CommonProvince> provinceLambdaQueryWrapper = new LambdaQueryWrapper<>();
        List<CommonProvince> provinces = provinceMapper.selectList(provinceLambdaQueryWrapper);
        return ResultVo.success(provinces);

    }
    // 市
    @GetMapping("/city/{code}")
    public ResultVo queryCity(@PathVariable String code){
        LambdaQueryWrapper<CommonCity> cityLambdaQueryWrapper = new LambdaQueryWrapper<>();
        cityLambdaQueryWrapper.eq(CommonCity::getProvincecode, code);
        List<CommonCity> cities = cityMapper.selectList(cityLambdaQueryWrapper);
        return ResultVo.success(cities);
    }
    // 区
    @GetMapping("/area/{code}")
    public ResultVo queryArea(@PathVariable String code){
        LambdaQueryWrapper<CommonArea> areaLambdaQueryWrapper = new LambdaQueryWrapper<>();
        areaLambdaQueryWrapper.eq(CommonArea::getCitycode, code);
        List<CommonArea> areas = areaMapper.selectList(areaLambdaQueryWrapper);
        return ResultVo.success(areas);
    }

    // 新增
    @Transactional
    @PostMapping("/insert")
    public ResultVo insert(@RequestBody WspAddr addr, HttpServletRequest request){
        /*if(ObjectUtil.isEmpty(addr.getAddrName())){
            return ResultVo.argsError("地址名为空");
        }else */if(ObjectUtil.isEmpty(addr.getReceiverName())){
            return ResultVo.argsError("收货人为空");
        }else if(ObjectUtils.isEmpty(addr.getReceiverPhone())){
            return ResultVo.argsError("收货人手机号为空");
        }else if(!validateMobilePhone(addr.getReceiverPhone())) {
            return ResultVo.argsError("手机号不规范");
        }else if(ObjectUtil.isEmpty(addr.getAddrContent())){
            return ResultVo.argsError("详细地址为空");
        }else if(ObjectUtil.isEmpty(addr.getProvinceCode())){
            return ResultVo.argsError("省 为空");
        }else if(ObjectUtil.isEmpty(addr.getCityCode())){
            return ResultVo.argsError("市 为空");
        }else if(ObjectUtil.isEmpty(addr.getAreaCode())){
            return ResultVo.argsError("区 为空");
        }

        // user id
        String tokenHeader = request.getHeader("token");
        String jsonToken = EncryptorUtil.decrypt(tokenHeader);
        AccessToken accessToken = JSONObject.parseObject(jsonToken, AccessToken.class);
        addr.setUserId(accessToken.getUserId());

        // 拼接展示用的字符串
        LambdaQueryWrapper<CommonProvince> commonProvinceLambdaQueryWrapper = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<CommonCity> commonCityLambdaQueryWrapper = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<CommonArea> commonAreaLambdaQueryWrapper = new LambdaQueryWrapper<>();
        commonProvinceLambdaQueryWrapper.eq(CommonProvince::getCode, addr.getProvinceCode());
        commonCityLambdaQueryWrapper.eq(CommonCity::getCode, addr.getCityCode());
        commonAreaLambdaQueryWrapper.eq(CommonArea::getCode, addr.getAreaCode());
        CommonProvince commonProvince = provinceMapper.selectOne(commonProvinceLambdaQueryWrapper);
        CommonCity commonCity = cityMapper.selectOne(commonCityLambdaQueryWrapper);
        CommonArea commonArea = areaMapper.selectOne(commonAreaLambdaQueryWrapper);
        //addr.setAreaName(commonProvince.getName() + "-" + commonCity.getName() + "-" + commonArea.getName());
        addr.setAreaName(commonProvince.getName() + commonCity.getName() + commonArea.getName());

        // 如果新增的是默认地址,调用清空默认地址方法
        if("1".equals(addr.getIsDef())){
            defReset(accessToken.getUserId());
        }

        // 存
        boolean save = service.save(addr);

        return save ? ResultVo.success():ResultVo.failed();

    }

    // 地址list
    @GetMapping("/queryList")
    public ResultVo queryList(HttpServletRequest request){
        // userid
        String tokenHeader = request.getHeader("token");
        String jsonToken = EncryptorUtil.decrypt(tokenHeader);
        AccessToken accessToken = JSONObject.parseObject(jsonToken, AccessToken.class);

        LambdaQueryWrapper<WspAddr> wspAddrLambdaQueryWrapper = new LambdaQueryWrapper<>();
        wspAddrLambdaQueryWrapper.eq(WspAddr::getUserId, accessToken.getUserId());
        List<WspAddr> wspAddrs = addrMapper.selectList(wspAddrLambdaQueryWrapper);

        return ResultVo.success(wspAddrs);
    }

    // 修改地址
    @Transactional
    @PostMapping("/update")
    public ResultVo updateAddr(@RequestBody WspAddrVO addr,HttpServletRequest request){
        /*if(ObjectUtil.isEmpty(addr.getAddrName())){
            return ResultVo.argsError("地址名为空");
        }else */if(ObjectUtil.isEmpty(addr.getReceiverName())){
            return ResultVo.argsError("收货人为空");
        }else if(ObjectUtils.isEmpty(addr.getReceiverPhone())){
            return ResultVo.argsError("收货人手机号为空");
        }else if(!validateMobilePhone(addr.getReceiverPhone())) {
            return ResultVo.argsError("手机号不规范");
        }else if(ObjectUtil.isEmpty(addr.getAddrContent())){
            return ResultVo.argsError("详细地址为空");
        }else if(ObjectUtil.isEmpty(addr.getProvinceCode())){
            return ResultVo.argsError("省 为空");
        }else if(ObjectUtil.isEmpty(addr.getCityCode())){
            return ResultVo.argsError("市 为空");
        }else if(ObjectUtil.isEmpty(addr.getAreaCode())){
            return ResultVo.argsError("区 为空");
        }

        String tokenHeader = request.getHeader("token");
        String jsonToken = EncryptorUtil.decrypt(tokenHeader);
        AccessToken accessToken = JSONObject.parseObject(jsonToken, AccessToken.class);

        // 拼接展示用的字符串
        LambdaQueryWrapper<CommonProvince> commonProvinceLambdaQueryWrapper = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<CommonCity> commonCityLambdaQueryWrapper = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<CommonArea> commonAreaLambdaQueryWrapper = new LambdaQueryWrapper<>();
        commonProvinceLambdaQueryWrapper.eq(CommonProvince::getCode, addr.getProvinceCode());
        commonCityLambdaQueryWrapper.eq(CommonCity::getCode, addr.getCityCode());
        commonAreaLambdaQueryWrapper.eq(CommonArea::getCode, addr.getAreaCode());
        CommonProvince commonProvince = provinceMapper.selectOne(commonProvinceLambdaQueryWrapper);
        CommonCity commonCity = cityMapper.selectOne(commonCityLambdaQueryWrapper);
        CommonArea commonArea = areaMapper.selectOne(commonAreaLambdaQueryWrapper);
        // addr.setAreaName(commonProvince.getName() + "-" + commonCity.getName() + "-" + commonArea.getName());
        addr.setAreaName(commonProvince.getName() + commonCity.getName() + commonArea.getName());


        // 如果新增的是默认地址,调用清空默认地址方法
        if("1".equals(addr.getIsDef())){
            defReset(accessToken.getUserId());
        }

        boolean b = service.updateById(addr);

        return b ? ResultVo.success():ResultVo.failed();
    }


    // 清除指定用户默认地址标记
    void defReset(String userId){
        LambdaQueryWrapper<WspAddr> wspAddrLambdaQueryWrapper = new LambdaQueryWrapper<>();
        wspAddrLambdaQueryWrapper.eq(WspAddr::getIsDef,"1");
        wspAddrLambdaQueryWrapper.eq(WspAddr::getUserId,userId);
        List<WspAddr> addrs = addrMapper.selectList(wspAddrLambdaQueryWrapper);
        for(WspAddr addr : addrs){
            addr.setIsDef("0");
            DozerBeanMapper dozerBeanMapper = new DozerBeanMapper();
            WspAddrVO addrVo = dozerBeanMapper.map(addr, WspAddrVO.class);
            log.info("addr清空 - {}", addr);
            log.info("addrVO清空 - {}", addrVo);
            service.updateById(addrVo);
        }

    }


    @GetMapping("/get/{id}")
    @ApiOperation(value = "获取单个", notes = "获取单个")
    @Override
    protected Result get(@PathVariable Long id) {
        return new Result().success(service.getInfoById(id));
    }



    // 手机号校验
    boolean validateMobilePhone(String in) {
        Pattern pattern = Pattern.compile("^[1]\\d{10}$");
        return pattern.matcher(in).matches();
    }








///////////////////////////////////////////////////////////////////////////////

    @PostMapping("/add")
    @ApiOperation(value = "添加", notes = "添加")
    @Override
    protected Result add(@RequestBody @Validated WspAddrVO dto) {
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

    @PutMapping("/edit")
    @ApiOperation(value = "编辑", notes = "编辑")
    @Override
    protected Result edit(@RequestBody @Validated WspAddrVO dto) {
        service.updateById(dto);
        return new Result().success("修改成功！");
    }

    @ApiOperation(value = "查询分页", notes = "查询分页")
    @PostMapping("/page")
    @Override
    protected Result page(@RequestBody @Validated WspAddrVO dto) {
        return new Result().success(service.page(dto));
    }


}