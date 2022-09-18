package com.unicorn.wsp.controller;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.unicorn.wsp.common.demandChange.DemandChange;
import com.unicorn.wsp.entity.WspOrder;
import com.unicorn.wsp.entity.exportvo.WspOrderBatchImportEto;
import com.unicorn.wsp.entity.vo.BatchImportVo;
import com.unicorn.wsp.service.KuaiDi100Service;
import com.unicorn.wsp.service.WspGiftCardService;
import com.unicorn.wsp.service.WspGoodsService;
import com.unicorn.wsp.utils.EasyExcelUtil;
import com.unicorn.wsp.utils.TokenUtils;
import com.unicorn.wsp.utils.ValidationUtil;
import com.unicorn.wsp.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.unicorn.wsp.common.base.BaseController;
import com.unicorn.wsp.common.result.Result;
import com.unicorn.wsp.entity.vo.WspOrderVO;
import com.unicorn.wsp.service.WspOrderService;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@Api(tags = {""})
@RequestMapping("/order")
public class WspOrderController
        extends BaseController<WspOrderService, WspOrderVO> {

    @Autowired
    private KuaiDi100Service kuaiDi100Service;

    @Autowired
    private WspGiftCardService wspGiftCardService;

    @Autowired
    private WspGoodsService wspGoodsService;

    @Autowired
    DemandChange demandChange;

    //文件上传目录
    @Value("${upload.dir}")
    private String uploadDirectory;

    // 图片访问路径前缀
    @Value("${upload.path}")
    private String path;


    @PostMapping("/add")
    @ApiOperation(value = "添加", notes = "添加")
    @Override
    protected Result add(@RequestBody @Validated WspOrderVO dto) {
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

    @Override
    protected Result edit(WspOrderVO dto) {
        return null;
    }

    @Override
    protected Result page(WspOrderVO dto) {
        return null;
    }


    // 上传图片 返回id
    @PostMapping("/upload")
    public ResultVo upload(@RequestParam MultipartFile file) throws IOException {

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
        return ResultVo.success(picPath);
    }

    /**
     * 批量空发货
     * */
    @PostMapping("/setBatchOrderStatusList")
    public ResultVo setBatchOrderStatusList(@RequestBody List<Integer> list){
        service.setBatchOrderStatusList(list);
        return ResultVo.success();
    }


    // zzn 单点发货
    @PostMapping("/sendOff")
    public ResultVo sendOff(@RequestBody EditLogisticsVo dto){
        if(ObjectUtil.isEmpty(dto)) {
            return ResultVo.failed("导入信息为空");
        }
        if(ObjectUtil.isEmpty(dto.getLogisticList())) {
            return ResultVo.failed("导入物流信息为空");
        }
        if(ObjectUtil.isEmpty(dto.getOrderId())){
            return ResultVo.failed("orderId为空");
        }
        int i = service.sendOff(dto);
        return 1==i ? ResultVo.success():ResultVo.failed();
    }

    // zzn 编辑发货
    @PostMapping("/editLogistics")
    public ResultVo editLogistics(@RequestBody EditLogisticsVo dto){
        if(ObjectUtil.isEmpty(dto)) {
            return ResultVo.failed("导入信息为空");
        }
        if(ObjectUtil.isEmpty(dto.getOrderId())){
            return ResultVo.failed("orderId为空");
        }
        int i = service.editLogistics(dto);
        return 1==i ? ResultVo.success(): i==2 ?ResultVo.failed("该订单未发货") : ResultVo.failed();
    }




    @PostMapping("/edit")
    @ApiOperation(value = "编辑", notes = "编辑")
    protected ResultVo editOrder(@RequestBody @Validated WspOrderVO dto) {
        boolean b = service.updateById(dto);
        if (b){
            return  ResultVo.success();

        }
        return  ResultVo.failed("修改失败");
    }

    @ApiOperation(value = "查询分页", notes = "查询分页")
    @PostMapping("/page")
    protected ResultVo pageOrder(@RequestBody @Validated WspOrderVO dto, HttpServletRequest request) {

        AccessToken accessToken = TokenUtils.analyseRequest(request);
        int roleId = accessToken.getRoleId();
        if(roleId == 2){
            dto.setComNum(accessToken.getComNum());
        }

        List<OrderQueryVo> orderPageList = service.getOrderPageList(dto);
        // 分页操作 zzn
        Integer total = orderPageList.size();
        List<OrderQueryVo> orderQueryVos = orderPageList.subList(dto.getPageSize() * (dto.getPageNum() - 1), ((dto.getPageSize() * dto.getPageNum()) > total ? total : (dto.getPageSize() * dto.getPageNum())));
        OrderQueryPageDto orderQueryPageDto = new OrderQueryPageDto();
        orderQueryPageDto.setOrderQueryVoList(orderQueryVos);
        orderQueryPageDto.setPageNum(dto.getPageNum());
        orderQueryPageDto.setPageSize(dto.getPageSize());
        orderQueryPageDto.setTotal(total);
        return  ResultVo.success(orderQueryPageDto);
    }

    /**
     * 导出excel
     * @param wspOrderVO
     * @param response
     * @return
     */
    @PostMapping("/exportExcel")
    protected ResultVo exportExcel(@RequestBody WspOrderVO wspOrderVO, HttpServletResponse response, HttpServletRequest request){

        AccessToken accessToken = TokenUtils.analyseRequest(request);
        int roleId = accessToken.getRoleId();
        if(roleId == 2){
            wspOrderVO.setComNum(accessToken.getComNum());
        }
        List<OrderQueryVo> orderPageList = service.getOrderPageList(wspOrderVO);
        List<WspOrderBatchImportEto> etosTest = new ArrayList<>();

        for (OrderQueryVo wspOrder : orderPageList) {
            //判断商品JSON是否为空
            wspOrder.setStatus(ValidationUtil.transferStatus(wspOrder.getStatus()));
            if (ObjectUtil.isNotEmpty(wspOrder.getOrderGoods())){
                String wuLiuNumber = "";
                String wuLiuCompany = "";
                //判断物流json是否为空
                if (ObjectUtil.isNotEmpty(wspOrder.getExpress())){
//                    List<BatchImportVo> batchImportVoList = JSONArray.parseArray(wspOrder.getLogistics(), BatchImportVo.class);
                    List<BatchImportVo> express = wspOrder.getExpress();
                    for (BatchImportVo batchImportVo : express) {
                        wuLiuNumber = wuLiuNumber.concat(","+batchImportVo.getCourierNumber());
                        wuLiuCompany = wuLiuCompany.concat(","+batchImportVo.getLogisticsCompany());
                    }
                    if (wuLiuCompany.length()>=1){
                        wuLiuCompany = wuLiuCompany.substring(1);
                    }
                    if (wuLiuNumber.length()>=1){
                        wuLiuNumber = wuLiuNumber.substring(1);
                    }
                }
//                List<OrderGoodsVo> orderGoodsVoList = JSONArray.parseArray(wspOrder.getGoodsList(), OrderGoodsVo.class);
                List<OrderGoodsVo> orderGoods = wspOrder.getOrderGoods();
                for (OrderGoodsVo orderGoodsVo : orderGoods) {
                    WspOrderBatchImportEto wspOrderBatchImportEto = new WspOrderBatchImportEto();
                    BeanUtils.copyProperties(wspOrder,wspOrderBatchImportEto);
                    wspOrderBatchImportEto.setGoodsName(orderGoodsVo.getGoodsName());
                    wspOrderBatchImportEto.setGoodsNum(orderGoodsVo.getGoodsNum());
                    wspOrderBatchImportEto.setCourierNumber(wuLiuNumber);
                    wspOrderBatchImportEto.setLogisticsCompany(wuLiuCompany);
                    wspOrderBatchImportEto.setUserPhone(wspOrder.getUserPhone());
                    wspOrderBatchImportEto.setGiftCardNumber(wspOrder.getGiftCardNumber());
                    etosTest.add(wspOrderBatchImportEto);
                }
            }else {
                WspOrderBatchImportEto wspOrderBatchImportEto = new WspOrderBatchImportEto();
                BeanUtils.copyProperties(wspOrder,wspOrderBatchImportEto);
                System.out.println("wspOrderBatchImportEto = " + wspOrderBatchImportEto);
                etosTest.add(wspOrderBatchImportEto);
            }
        }
        String fileName = "records";
        String sheetName = "sheet1";
        try {
            EasyExcelUtil.writeExcel(response, etosTest, fileName, sheetName, new WspOrderBatchImportEto());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  ResultVo.success();
    }

    /**
     * 批量导入物流
     * @param file
     * @return
     * @throws IOException
     */
    @PostMapping("/importInfo")
    public Result importInfo(@RequestParam("file") MultipartFile file) throws IOException {
        return service.batchShipment(file);
    }

    /**
     * Status 0:待发货
     * 1：已发货
     * 2：取消订单
     * 3：待退货
     * 4：待换货
     * 5：已退货
     * 6：已换货
     * 7：
     * @param wspOrderVO
     * @param request
     * @return
     */
    @PostMapping("/get/getOrder")
    @ApiOperation(value = "用户订单查询", notes = "用户订单查询")
    protected ResultVo getOrder(@RequestBody WspOrderVO wspOrderVO, HttpServletRequest request) {
        String header = request.getHeader("postman-token");
        return  ResultVo.success(service.getOrderListToken(wspOrderVO,request));
    }

    @GetMapping("/queryLogistics")
    @ApiOperation(value = "物流查询", notes = "物流查询")
    public ResultVo findOrder(@RequestParam(name = "orderId") String orderId) throws Exception {
        if(StringUtils.isEmpty(orderId)){
            return ResultVo.failed("订单为空");
        }
        JSONObject object = kuaiDi100Service.findOrder(orderId);

        if(object.getString("message").equals("ok")){
            return  ResultVo.success(object.getString("data"));
        }else{
            return  ResultVo.failed(object.getString("message"));
        }
    }

    @PostMapping("/createOrder")
    @ApiOperation(value = "下单", notes = "下单")
    protected ResultVo createOrder(@RequestBody WspOrderVO wspOrderVO, HttpServletRequest request) {
        if (ObjectUtil.isEmpty(wspOrderVO.getOrderAddress())){
            return ResultVo.argsError("地址参数为空");
        }
        // 1 判断需求（下单只能选一个种类商品）是否启用
        boolean b = demandChange.queryDemand(1, 1);
        if(b){
            // 2 判断是否只选择了一个种类
            boolean onlyType = demandChange.checkGiftType(wspOrderVO);
            if(!onlyType){
                return ResultVo.failed("只能兑换一个商品分类");
            }
        }
        String userId = TokenUtils.analyseRequest(request).getUserId();
        Integer order = service.createOrder(wspOrderVO, userId);
        return returnRes(order);
    }


    @GetMapping("/cancelOrder/{id}")
    @ApiOperation(value = "编辑", notes = "编辑")
    protected ResultVo cancelOrder(@PathVariable Integer id) {
        int i = demandChange.queryDemand(5);
        if( i != 1 ) return ResultVo.failed("取消订单功能已禁用");
        WspOrderVO wspOrderVO = new WspOrderVO();
        wspOrderVO.setOrderId(id);
        wspOrderVO.setStatus("2");
        boolean b = service.updateById(wspOrderVO);
        if (b){
            WspOrder infoById = service.getInfoById(Long.parseLong(id.toString()));
            if (ObjectUtil.isNotEmpty(infoById.getCardNumber())){
                wspGiftCardService.reSetCard(infoById.getCardNumber());
            }
            List<OrderGoodsVo> orderGoodsVoList = JSONArray.parseArray(infoById.getGoodsList(), OrderGoodsVo.class);
            if (ObjectUtil.isNotEmpty(orderGoodsVoList)){
                for (OrderGoodsVo orderGoodsVo : orderGoodsVoList) {
                    System.out.println("(~(orderGoodsVo.a() - 1)) = " + (~(orderGoodsVo.getGoodsNum() - 1)));
                    wspGoodsService.inventoryReduction(orderGoodsVo.getGoodsId(),(~(orderGoodsVo.getGoodsNum() - 1)) );
                }
            }
            return  ResultVo.success();
        }
        return  ResultVo.failed("修改失败");
    }

    @PostMapping("/rollBackOrder")
    @ApiOperation(value = "退换货", notes = "退换货")
    public ResultVo rollBackOrder(@RequestBody WspOrderVO wspOrderVO) {
        boolean b = service.updateById(wspOrderVO);
        if (b){
            return  ResultVo.success();
        }
        return  ResultVo.failed("修改失败");
    }

    @PostMapping("/addCommentOrReturnReason")
    @ApiOperation(value = "退换货原因&评价", notes = "退换货&评价")
    public ResultVo addComment(@RequestBody WspOrderVO wspOrderVO) {
        boolean b = service.updateById(wspOrderVO);
        if (b){
            return  ResultVo.success();
        }
        return  ResultVo.failed("修改失败");
    }

    @PostMapping("/changeOrderAdmin")
    @ApiOperation(value = "换货", notes = "换货")
    public ResultVo revertOrder(@RequestBody WspOrderVO wspOrderVO) {
        boolean b;
        WspOrder wspOrderTemp = service.getInfoById(Long.parseLong(wspOrderVO.getOrderId().toString()));
        if(wspOrderVO.getStatus().equals("4")) {//换货
            wspOrderVO.setStatus("6");//已换货
            b = service.updateById(wspOrderVO);
            wspOrderTemp.setOrderId(null);
            wspOrderTemp.setStatus("0");//新增新订单 原有参数除订单号之外不变
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            wspOrderTemp.setOrderNumber(sdf.format(new Date()));
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            wspOrderTemp.setOrderTime(format.format(new Date()));
            service.save(wspOrderTemp);
        }else{//退货
            wspOrderVO.setStatus("5");
            service.updateById(wspOrderVO);
            if (ObjectUtil.isNotEmpty(wspOrderTemp.getCardNumber())){
                wspGiftCardService.reSetCard(wspOrderTemp.getCardNumber());
            }
            List<OrderGoodsVo> orderGoodsVoList = JSONArray.parseArray(wspOrderTemp.getGoodsList(), OrderGoodsVo.class);
            if (ObjectUtil.isNotEmpty(orderGoodsVoList)){
                for (OrderGoodsVo orderGoodsVo : orderGoodsVoList) {
                    System.out.println("(~(orderGoodsVo.a() - 1)) = " + (~(orderGoodsVo.getGoodsNum() - 1)));
                    wspGoodsService.inventoryReduction(orderGoodsVo.getGoodsId(),(~(orderGoodsVo.getGoodsNum() - 1)) );
                }
            }
            return  ResultVo.success();
        }
        if (b){
            return  ResultVo.success();
        }
        return  ResultVo.failed("修改失败");
    }

    @PostMapping("/createOrderCheck")
    protected ResultVo createOrderCheck(@RequestBody WspOrderVO wspOrderVO, HttpServletRequest request) {
        // 1 判断需求（下单只能选一个种类商品）是否启用
        boolean b = demandChange.queryDemand(1, 1);
        if(b){
            // 2 判断是否只选择了一个种类
            boolean onlyType = demandChange.checkGiftType(wspOrderVO);
            if(!onlyType){
                return ResultVo.failed("只能兑换一个商品分类");
            }
        }
        AccessToken accessToken = TokenUtils.analyseRequest(request);
        int i = service.quotaIsSpent(wspOrderVO, accessToken.getUserId());
        return returnRes(i);
    }



    ResultVo returnRes(int i){
        switch (i){
            case 0: return ResultVo.failed("更新数据库失败");
            case 1: return ResultVo.success("下单成功");
            case 2: return ResultVo.failed("余额不足");
            case 3: return ResultVo.argsError("缺少参数");
            case 4: return ResultVo.failed("订单号重复，系统错误");
            case 5: return ResultVo.failed("库存不足");
            case 6: return ResultVo.BALANCE_BE_ENOUGH("余额未使用完");
            case 888: return ResultVo.success("通过");
            case 999: return ResultVo.failed("单品数量超出额度倍数");
            default: return ResultVo.failed("系统异常");
        }
    }


}