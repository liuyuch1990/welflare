package com.unicorn.wsp.controller;

import cn.hutool.core.util.ObjectUtil;
import com.unicorn.wsp.entity.WspGoods;
import com.unicorn.wsp.entity.WspGoodsPic;
import com.unicorn.wsp.entity.zznvo.GoodsVo;
import com.unicorn.wsp.entity.zznvo.PicVo;
import com.unicorn.wsp.service.WspComService;
import com.unicorn.wsp.service.WspGoodsPicService;
import com.unicorn.wsp.utils.TokenUtils;
import com.unicorn.wsp.vo.AccessToken;
import com.unicorn.wsp.vo.PageVo;
import com.unicorn.wsp.vo.ResultVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.unicorn.wsp.common.base.BaseController;
import com.unicorn.wsp.common.result.Result;
import com.unicorn.wsp.entity.vo.WspGoodsVO;
import com.unicorn.wsp.service.WspGoodsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@Api(tags = {"商品"})
@RequestMapping("/goods")
public class WspGoodsController
	extends BaseController<WspGoodsService, WspGoodsVO> {

    @Autowired
    WspGoodsPicService picService;

    @Autowired
    WspGoodsService goodsService;

    @Autowired
    WspComService comService;



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

        WspGoodsPic pic = new WspGoodsPic();
        pic.setPicId(uuidPIC);
        pic.setPicRealname(originalFilename);
        pic.setPicSavepath(path + newFileName);
        pic.setPicSavepath(picPath);
        //空著商品id 是否爲封面  排序pic

        picService.save(pic);

        return ResultVo.success(uuidPIC);
    }

    // 上架商品，同時吧圖片和商品對應上
    @Transactional
    @PostMapping("/addGoods")
    public ResultVo addGoods(@RequestBody PicVo goods){

        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        //
        List<WspGoodsPic> pics = goods.getPics();
        WspGoodsPic pic1 = new WspGoodsPic();
        for (WspGoodsPic pic: pics){
            pic1.setPicId(pic.getPicId());
            pic1.setPicSort(pic.getPicSort());
            pic1.setIsCover(pic.getIsCover());
            pic1.setGoodsId(uuid);
            picService.updateById(pic1);
        }
        // 類轉換
        Mapper dozerMapper = new DozerBeanMapper();
        WspGoods map = dozerMapper.map(goods, WspGoods.class);
        map.setGoodsId(uuid);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        map.setCreatedDate(format.format(new Date()));
        map.setIsDel("0");
        map.setGoodsSales(0);

        boolean save = service.save(map);
        if(save) {
            return ResultVo.success();
        }else {
            return ResultVo.failed();
        }
    }

    /**
     * 商品列表
     * 迭代修改：类型list限制添加进条件查询
     * */
    @PostMapping("/list")
    public ResultVo queryList(@RequestBody GoodsVo dto, HttpServletRequest request){
        AccessToken token = TokenUtils.analyseRequest(request);
        List<String> list = comService.queryTypeListByCom(token.getComNum());
        dto.setTypeList(list);
        PageVo<PicVo> page = service.page1(dto);
        return ResultVo.success(page);
    }

    // 商品詳情
    @GetMapping("/queryById/{id}")
    public ResultVo queryList(@PathVariable String id){
        // 查没有下架的商品信息by goodid
        List<WspGoods> wspGoodsList = goodsService.queryById(id);
        log.info("查没有下架的商品信息 by goodid - {}", wspGoodsList);
        WspGoods goods = new WspGoods();
        if(ObjectUtil.isNotEmpty(wspGoodsList)){
            goods = wspGoodsList.get(0);
        }else{
            return ResultVo.failed("没有此商品");
        }
        // 查封面图 by goodid
        List<WspGoodsPic> wspGoodsPics = picService.queryListByGoodId(id);
        // 類轉換
        Mapper dozerMapper = new DozerBeanMapper();
        PicVo goods1 = dozerMapper.map(goods, PicVo.class);
        // 圖片賦給picvo
        for(WspGoodsPic pic: wspGoodsPics){
            if("1".equals(pic.getIsCover())){
                goods1.setCoverPath(pic.getPicSavepath());
            }
        }
        goods1.setPics(wspGoodsPics);
        return ResultVo.success(goods1);

    }


    // 刪除商品
    @GetMapping("/delete/{goodsId}")
    public void delGoods(@PathVariable String goodsId){
        goodsService.delGoods(goodsId);
    }


    // 清空不该存在的图片
    @GetMapping("/clearPic")
    public void clearPic(){
        goodsService.clearPic();
    }


    @GetMapping("/test/{goodsId}/{num}")
    public boolean test(@PathVariable String goodsId,@PathVariable Integer num){
        boolean b = goodsService.inventoryReduction(goodsId, num);
        return  b;
    }


    /**
     * 编辑商品
     * */
    @PostMapping("/editGoods")
    public ResultVo editGoods(@RequestBody WspGoods goods){
        boolean b = service.editGoods(goods);
        return b? ResultVo.success():ResultVo.failed();
    }


















    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @PostMapping("/add")
    @ApiOperation(value = "添加", notes = "添加")
    @Override
    protected Result add(@RequestBody @Validated WspGoodsVO dto) {
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
    protected Result edit(@RequestBody @Validated WspGoodsVO dto) {
        service.updateById(dto);
        return new Result().success("修改成功！");
    }

    @ApiOperation(value = "查询分页", notes = "查询分页")
    @PostMapping("/page")
    @Override
    protected Result page(@RequestBody @Validated WspGoodsVO dto) {
        return new Result().success(service.page(dto));
    }






}