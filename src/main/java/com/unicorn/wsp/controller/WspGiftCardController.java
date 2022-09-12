package com.unicorn.wsp.controller;


import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import com.unicorn.wsp.common.demandChange.DemandChange;
import com.unicorn.wsp.common.exception.BusinessRollbackException;
import com.unicorn.wsp.entity.WspGiftCard;

import com.unicorn.wsp.entity.vo.WspOrderVO;
import com.unicorn.wsp.entity.zznvo.ExchangeCode;
import com.unicorn.wsp.entity.zznvo.GiftCardPageVo;
import com.unicorn.wsp.entity.zznvo.GiftCardVo;

import com.unicorn.wsp.mapper.WspCardQuotaMapper;
import com.unicorn.wsp.mapper.WspGiftCardMapper;
import com.unicorn.wsp.service.WspCardQuotaService;
import com.unicorn.wsp.service.WspCardTypeService;
import com.unicorn.wsp.utils.EncryptorUtil;
import com.unicorn.wsp.utils.TokenUtils;
import com.unicorn.wsp.utils.ZExcelUtil;
import com.unicorn.wsp.vo.AccessToken;
import com.unicorn.wsp.vo.PageVo;
import com.unicorn.wsp.vo.ResultVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.unicorn.wsp.common.base.BaseController;
import com.unicorn.wsp.common.result.Result;
import com.unicorn.wsp.entity.vo.WspGiftCardVO;
import com.unicorn.wsp.service.WspGiftCardService;
import jdk.nashorn.internal.parser.Token;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@Slf4j
@RestController
@Api(tags = {""})
@RequestMapping("/gift/card")
public class WspGiftCardController
	extends BaseController<WspGiftCardService, WspGiftCardVO> {




    @Value("${download.dir}")
    String downloadFilePath;

    @Autowired
    WspGiftCardService cardService;

    @Autowired
    WspCardQuotaMapper quotaMapper;

    @Autowired
    WspCardQuotaService quotaService;

    @Autowired
    WspGiftCardMapper cardMapper;

    @Autowired
    DemandChange demandChange;

    @Autowired
    WspCardTypeService typeService;

    /**
     * 清除礼品卡绑定状态
     * */
    @GetMapping("/unbound/{giftCardId}")
    public ResultVo unbound(@PathVariable int giftCardId){
        boolean b = cardService.unbound(giftCardId);
        return b?ResultVo.success():ResultVo.failed();
    }


    /**
     * 迭代：导入礼品卡（非礼品卡种类）
     * */
    @Transactional
    @PostMapping("importExcel")
    public ResultVo importExcel(@RequestPart("file") MultipartFile file,
                                @RequestParam("cardTypeId") String cardTypeId,
                                HttpServletRequest request) {

        List<ExchangeCode> importCardList = ZExcelUtil.importExcel(file, 1, 1, ExchangeCode.class);
        // 也可以使用MultipartFile,使用 FileUtil.importExcel(MultipartFile file, Integer titleRows, Integer headerRows, Class<T> pojoClass)导入
        System.out.println("导入数据一共【" + importCardList.size() + "】行");


        /*
         * 把拿到的字段 & list 赋到list<gift_card>
         */
        WspGiftCard wspGiftCard = new WspGiftCard();

        AccessToken accessToken = TokenUtils.analyseRequest(request);
        String comNum = accessToken.getComNum();

        // set 公司编号
        wspGiftCard.setComNum(comNum);

        // 判断里礼品卡是否存在,并set
        List<String> cardTypeList = typeService.queryCardTypeList(comNum);
        boolean contains = cardTypeList.contains(cardTypeId);
        if (!contains) {
            return ResultVo.argsError("不存在该卡");
        }
        wspGiftCard.setCardTypeId(cardTypeId);

        // 上传的excel 兑换码 唯一校验
        HashSet<String> set = new HashSet<>();

        for (ExchangeCode e : importCardList) {
            // 顺便trim一下
            e.setExchangeCode(e.getExchangeCode().trim());
            log.info("setExchangeCode - {}", e.getExchangeCode());
            // 验证一下数据库有没有重复的
            LambdaQueryWrapper<WspGiftCard> cardWrapper = new LambdaQueryWrapper<>();
            cardWrapper.ne(WspGiftCard::getIsDel, "1");
            cardWrapper.eq(WspGiftCard::getGiftCardNum, e.getExchangeCode());
            Integer count = cardMapper.selectCount(cardWrapper);
            log.info("数据库查count - {} ", count);
            if (count > 0) {
                return ResultVo.failed("数据库中存在重复卡号: " + e.getExchangeCode());
            } else {
                set.add(e.getExchangeCode());
            }
        }
        if (set.size() != importCardList.size()) {
            return ResultVo.argsError("导入数据存在相同兑换码");
        }

        // 遍历 导入的日期 卡号 额度系数 并set
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String format = "";

        for (ExchangeCode e : importCardList) {

            //log.info("excel导入数据 - {}", e);
            try {
                //set date
                format = simpleDateFormat.format(e.getValid_date());
                //log.info("excel date转换 - {}" , format);
                wspGiftCard.setValidDate(format);
            } catch (Exception ex) {
                // ex.printStackTrace();
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return ResultVo.argsError("导入文件存在不正确日期格式或空日期");
            }
            // set 兑换码
            wspGiftCard.setGiftCardNum(e.getExchangeCode());
            // set 额度系数
            wspGiftCard.setQuotaMultiple(e.getQuotaMultiple());
            // isUse 0 未兑换
            wspGiftCard.setIsUse("0");
            // isTrue
            String isTrue = service.changeValid(format);
            wspGiftCard.setIsTrue(isTrue);

            boolean save = cardService.save(wspGiftCard);
            if (!save) {
                throw new BusinessRollbackException("兑换码为 " + e.getExchangeCode() + "的礼品卡导入失败");
            }
        }

        return ResultVo.success();

    }


    // 礼品卡有效期编辑
    @PostMapping("/editDate")
    public ResultVo editDate(@RequestBody WspGiftCard vo){
        if(ObjectUtil.isNull(vo.getGiftCardId())){
            return ResultVo.argsError("缺少id");
        }
        if(ObjectUtil.isNull(vo.getValidDate())){
            return ResultVo.argsError("日期不能为空");
        }

        // 日期校验挺费劲，暂时交给前端了

        int i = quotaService.updateDate(vo);
        return i > 0 ? ResultVo.success():ResultVo.failed();
    }


    /**
     * 迭代：分页礼品卡list
     * */
    @PostMapping("/queryCardList")
    public ResultVo queryCardList(@RequestBody GiftCardPageVo vo, HttpServletRequest request){
        AccessToken accessToken = TokenUtils.analyseRequest(request);
        int roleId = accessToken.getRoleId();
        if(roleId == 2){
            vo.setComNum(accessToken.getComNum());
        }
        log.info("cardTypeId - {}", vo.getPageNum());

        PageVo<GiftCardVo> giftCardVos = cardService.queryCardInfoList(vo);

        return ResultVo.success(giftCardVos);

    }


    /**
     * 迭代：用户角度查礼品卡(余额)
     * */
    @GetMapping("/wallet")
    public ResultVo wallet(HttpServletRequest request){
        // userid
        String tokenHeader = request.getHeader("token");
        String jsonToken = EncryptorUtil.decrypt(tokenHeader);
        AccessToken accessToken = JSONObject.parseObject(jsonToken, AccessToken.class);
        log.info("用户查礼品卡-userid - {}",accessToken.getUserId());
        HashMap<String, Object> giftCardVos = cardService.queryCardByUserId(accessToken.getUserId());
        return ResultVo.success(giftCardVos);
    }



    /**
     * 迭代修改：用户绑定礼品卡（只能添加自己公司的礼品卡
     * */
    @GetMapping("exchange/{cardNum}")
    public ResultVo exchange(@PathVariable("cardNum") String cardNum,HttpServletRequest request){
        // userid
        String tokenHeader = request.getHeader("token");
        String jsonToken = EncryptorUtil.decrypt(tokenHeader);
        AccessToken accessToken = JSONObject.parseObject(jsonToken, AccessToken.class);

        // 补充必要业务
        boolean b = demandChange.queryDemand(2, 1);
        if(b){
            WspOrderVO wspOrderVO = new WspOrderVO();
            wspOrderVO.setStatus("0");
            if(!demandChange.checkOrderState(wspOrderVO, request)){
                return ResultVo.failed("无法绑定礼品卡（原因：存在未进行完毕的订单）");
            }
        }

        // 临时 不让用户添加多张卡片
        Integer s = cardService.onlyOneCard(accessToken.getUserId());
        if(s > 0){
            return ResultVo.failed("无法绑定礼品卡（原因：存在没使用的礼品卡）");
        }

        boolean exist = cardService.isExist(cardNum, accessToken.getComNum());
        if(exist){
            int exchange = cardService.exchange(cardNum,accessToken.getUserId(), accessToken.getPhone());
            return exchange>0 ? ResultVo.success("兑换成功"):ResultVo.failed("兑换失败");
        }else {
            return ResultVo.failed("卡号不存在或已过期");
        }
    }


    // 下载模板
    @GetMapping("/templateDownload")
    public void templateDownload(HttpServletResponse response){
        String fileName = "template.xlsx";//被下载文件的名称

        File file = new File(downloadFilePath);
        if (file.exists()) {
            response.setContentType("application/force-download");// 设置强制下载不打开
            response.addHeader("Content-Disposition", "attachment;fileName=" + fileName);
            byte[] buffer = new byte[1024];
            FileInputStream fis = null;
            BufferedInputStream bis = null;
            try {
                fis = new FileInputStream(file);
                bis = new BufferedInputStream(fis);
                OutputStream outputStream = response.getOutputStream();
                int i = bis.read(buffer);
                while (i != -1) {
                    outputStream.write(buffer, 0, i);
                    i = bis.read(buffer);
                }

                log.info("下载成功");
                return;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (bis != null) {
                    try {
                        bis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        log.info("下载失败");
    }


/*
    @GetMapping("test/{num}")
    public ResultVo testReSet(@PathVariable  String num){
        boolean b = cardService.reSetCard(num);

        return ResultVo.success(b);
    }
*/
















































    /**
     * 导入礼品卡（非礼品卡种类）
     * */
 /*   @Transactional
    @PostMapping("importExcel-old")
    public ResultVo importExcel(@RequestPart("file") MultipartFile file,
                                @RequestParam("cardName") String cardTypeId,
                                HttpServletRequest request) {

        List<ExchangeCode> importCardList = ZExcelUtil.importExcel(file, 1, 1, ExchangeCode.class);
        // 也可以使用MultipartFile,使用 FileUtil.importExcel(MultipartFile file, Integer titleRows, Integer headerRows, Class<T> pojoClass)导入
        System.out.println("导入数据一共【" + importCardList.size() + "】行");


        // ****把拿到的字段 & list 赋到list<gift_card>***

        WspGiftCard wspGiftCard = new WspGiftCard();

        AccessToken accessToken = TokenUtils.analyseRequest(request);
        String comNum = accessToken.getComNum();

        // set 公司编号
        wspGiftCard.setComNum(comNum);

        // 判断里礼品卡是否存在,并set
        List<String> cardTypeList = typeService.queryCardTypeList(comNum);
        boolean contains = cardTypeList.contains(cardTypeId);
        if (!contains) {
            return ResultVo.argsError("不存在该卡");
        }
        wspGiftCard.setCardTypeId(cardTypeId);

        // 上传的excel 兑换码 唯一校验
        HashSet<String> set = new HashSet<>();

        for (ExchangeCode e : importCardList) {
            // 顺便trim一下
            e.setExchangeCode(e.getExchangeCode().trim());
            log.info("setExchangeCode - {}", e.getExchangeCode());
            // 验证一下数据库有没有重复的
            LambdaQueryWrapper<WspGiftCard> cardWrapper = new LambdaQueryWrapper<>();
            cardWrapper.ne(WspGiftCard::getIsDel, "1");
            cardWrapper.eq(WspGiftCard::getGiftCardNum, e.getExchangeCode());
            Integer count = cardMapper.selectCount(cardWrapper);
            log.info("数据库查count - {} ", count);
            if (count > 0) {
                return ResultVo.failed("数据库中存在重复卡号: " + e.getExchangeCode());
            } else {
                set.add(e.getExchangeCode());
            }
        }
        if (set.size() != importCardList.size()) {
            return ResultVo.argsError("导入数据存在相同兑换码");
        }

        // 遍历 导入的日期 卡号 额度系数 并set
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String format = "";

        for (ExchangeCode e : importCardList) {

            //log.info("excel导入数据 - {}", e);
            try {
                //set date
                format = simpleDateFormat.format(e.getValid_date());
                //log.info("excel date转换 - {}" , format);
                wspGiftCard.setValidDate(format);
            } catch (Exception ex) {
                // ex.printStackTrace();
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return ResultVo.argsError("导入文件存在不正确日期格式或空日期");
            }
            // set 兑换码
            wspGiftCard.setGiftCardNum(e.getExchangeCode());
            // set 额度系数
            wspGiftCard.setQuotaMultiple(e.getQuotaMultiple());
            // isUse 0 未兑换
            wspGiftCard.setIsUse("0");

            boolean save = cardService.save(wspGiftCard);
            if (!save) {
                return ResultVo.failed("兑换码为 " + e.getExchangeCode() + "的礼品卡导入失败");
            }
        }

        return ResultVo.success();

    }

    */















/////////////////////////////////////////////////////////////////////////////////
    @PostMapping("/add")
    @ApiOperation(value = "添加", notes = "添加")
    @Override
    protected Result add(@RequestBody @Validated WspGiftCardVO dto) {
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
        return new Result().success(service.getInfoById(id+""));
    }

    @PutMapping("/edit")
    @ApiOperation(value = "编辑", notes = "编辑")
    @Override
    protected Result edit(@RequestBody @Validated WspGiftCardVO dto) {
        service.updateById(dto);
        return new Result().success("修改成功！");
    }

    @ApiOperation(value = "查询分页", notes = "查询分页")
    @PostMapping("/page")
    @Override
    protected Result page(@RequestBody @Validated WspGiftCardVO dto) {
        return new Result().success(service.page(dto));
    }


}