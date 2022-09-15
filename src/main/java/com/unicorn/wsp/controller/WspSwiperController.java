package com.unicorn.wsp.controller;

import com.unicorn.wsp.common.base.BaseController;
import com.unicorn.wsp.common.result.Result;
import com.unicorn.wsp.entity.vo.WspSwiperVO;
import com.unicorn.wsp.service.WspGoodsPicService;
import com.unicorn.wsp.service.WspSwiperService;
import com.unicorn.wsp.vo.ResultVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@RestController
@Api(tags = {"商品"})
@RequestMapping("/swiper")
public class WspSwiperController
	extends BaseController<WspSwiperService, WspSwiperVO> {

    @Autowired
    WspGoodsPicService picService;

    @Autowired
    WspSwiperService goodsService;

    @Autowired
    WspSwiperService wspSwiperService;



    //文件上传目录
    @Value("${upload.dir}")
    private String uploadDirectory;

    // 图片访问路径前缀
    @Value("${upload.path}")
    private String path;

    // 上传图片 返回id
    @PostMapping("/upload")
    public ResultVo upload(MultipartFile file) throws IOException {

        // 获取上传的文件名称，并结合存放路径，构建新的文件名称
        String originalFilename = file.getOriginalFilename();

        // 为防止文件名重名，拿到原文件名后对其进行替换
        String suffix = FilenameUtils.getExtension(originalFilename);
        String uuidPIC = UUID.randomUUID().toString().replaceAll("-", "");
        String newFileName = uuidPIC + "." + suffix;

        // 上传路径，默认是d:/upload
        File uploadDir = new File(this.uploadDirectory);

        // 判断路径是否存在，不存在则新创建一个
        if(!uploadDir.exists()){
            uploadDir.mkdir();
        }

        //创建目标文件
        File newFile = new File(this.uploadDirectory,newFileName);
        file.transferTo(newFile);
        String picPath = path + newFileName;

        log.info("图片地址 - {}",picPath);

//        WspGoodsPic pic = new WspGoodsPic();
//        pic.setPicId(uuidPIC);
//        pic.setPicRealname(originalFilename);
//        pic.setPicSavepath(path + newFileName);
//        pic.setPicSavepath(picPath);
//        //空著商品id 是否爲封面  排序pic
//
//        picService.save(pic);

        return ResultVo.success(picPath);
    }

    @PostMapping("/add")
    @ApiOperation(value = "添加", notes = "添加")
    @Override
    protected Result add(@RequestBody @Validated WspSwiperVO dto) {
        service.save(dto);
        return new Result().success("创建/修改成功！");
    }

    @Override
    protected Result delete(Long id) {
        return null;
    }

    @Override
    protected Result get(Long id) {
        return null;
    }

    @GetMapping("/delete/{id}")
    @ApiOperation(value = "删除", notes = "删除")
    protected Result delete(@PathVariable String id) {
        service.removeById(id);
        return new Result().success("删除成功！");
    }

    @GetMapping("/get/{id}")
    @ApiOperation(value = "获取单个", notes = "获取单个")
    protected Result get(@PathVariable String id) {
        return new Result().success(service.getInfoById(id));
    }

    @PostMapping("/edit")
    @ApiOperation(value = "编辑", notes = "编辑")
    protected Result edit(@RequestBody @Validated WspSwiperVO dto) {
        service.updateById(dto);
        return new Result().success("修改成功！");
    }

    @ApiOperation(value = "查询分页", notes = "查询分页")
    @PostMapping("/page")
    @Override
    protected Result page(@RequestBody @Validated WspSwiperVO dto) {
        return new Result().success(service.page(dto));
    }

}