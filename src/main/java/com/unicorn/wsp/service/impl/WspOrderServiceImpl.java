package com.unicorn.wsp.service.impl;



import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.unicorn.wsp.common.annotation.Excel;
import com.unicorn.wsp.common.demandChange.DemandChange;
import com.unicorn.wsp.common.exception.BusinessRollbackException;
import com.unicorn.wsp.common.result.Result;
import com.unicorn.wsp.entity.WspGiftCard;
import com.unicorn.wsp.entity.WspGoods;
import com.unicorn.wsp.entity.WspOrder;
import com.unicorn.wsp.entity.WspUser;
import com.unicorn.wsp.entity.exportvo.WspOrderBatchImportEto;
import com.unicorn.wsp.entity.exportvo.WspOrderETO;
import com.unicorn.wsp.entity.vo.BatchImportVo;
import com.unicorn.wsp.entity.vo.WspOrderVO;
import com.unicorn.wsp.entity.zznvo.GiftCardVo;
import com.unicorn.wsp.mapper.WspOrderMapper;
import com.unicorn.wsp.service.*;
import com.unicorn.wsp.utils.EncryptorUtil;
import com.unicorn.wsp.utils.ExcelUtil;
import com.unicorn.wsp.utils.Object2ListMapUtil;
import com.unicorn.wsp.utils.TokenUtils;
import com.unicorn.wsp.vo.*;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.nutz.lang.Mirror;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.*;

/**
* <p>
    *  ?????????
    * </p>
*
* @author spark
* @since 1.0
*/
@Slf4j
@Service
public class WspOrderServiceImpl extends ServiceImpl<WspOrderMapper,WspOrder>
    implements WspOrderService {

    @Autowired
    private WspOrderMapper wspOrderMapper;

    @Autowired
    private WspGoodsPicService wspGoodsPicService;

    @Autowired
    private WspGoodsService wspGoodsService;

    @Autowired
    private WspGiftCardService wspGiftCardService;

    @Autowired
    private WspUserService wspUserService;

    @Autowired
    DemandChange demandChange;


    @Override
    public WspOrder getInfoById(Long id) {
        return super.getById(id);
    }

    @Override
    public boolean save(WspOrderVO dto) {
        WspOrder entity = new WspOrder();
        BeanUtils.copyProperties(dto, entity);
        return super.save(entity);
    }

    @Override
    public boolean updateById(WspOrderVO dto) {
        WspOrder entity = new WspOrder();
        BeanUtils.copyProperties(dto, entity);
        return super.updateById(entity);
    }

    @Override
    public boolean removeById(Long id) {
        return super.removeById(id);
    }

    @Override
    public IPage<WspOrder> page(WspOrderVO dto) {
        LambdaQueryWrapper<WspOrder> queryWrapper = new LambdaQueryWrapper<>();
        if (ObjectUtil.isNotNull(dto)) {

        }
        Page<WspOrder> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        return this.page(page, queryWrapper);
    }



    /**
     * ???????????????
     * */
    @Transactional
    public boolean setBatchOrderStatusList(List<Integer> list){
        list.forEach(id->{
            LambdaQueryWrapper<WspOrder> wrapper = new LambdaQueryWrapper<>();
            // wrapper.ne(WspOrder::getIsDel,"1");
            wrapper.eq(WspOrder::getOrderId,id);
            wrapper.eq(WspOrder::getStatus,0);

            WspOrder wspOrder = new WspOrder();
            wspOrder.setStatus("1");
            int i = wspOrderMapper.update(wspOrder,wrapper);
            if(i > 1) throw new BusinessRollbackException("??????????????????");
        });
        return true;

    }



    // zzn ????????????
    public int editLogistics(EditLogisticsVo dto){
        LambdaQueryWrapper<WspOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WspOrder::getOrderId, dto.getOrderId());

        WspOrder order = null;
        try{
            order = wspOrderMapper.selectOne(queryWrapper);
        }catch (Exception e){
            log.info("??????????????? --> {}", dto.getOrderId());
            return 0;
        }
        if(ObjectUtil.isEmpty(order)) return 0;

        if(!"1".equals(order.getStatus())){
            return 2;
        }
        if(ObjectUtil.isEmpty(order)){
            return 0;
        }

        order.setLogistics(JSON.toJSONString(dto.getLogisticList()));
        return wspOrderMapper.updateById(order)==1 ? 1 : 0;
    }

    // zzn ????????????
    @Transactional
    public int sendOff(EditLogisticsVo dto){
        LambdaQueryWrapper<WspOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WspOrder::getOrderId, dto.getOrderId());
        WspOrder order = null;
        try{
            order = wspOrderMapper.selectOne(queryWrapper);
        }catch (Exception e){
            log.info("??????????????? --> {}", dto.getOrderId());
            return 0;
        }
        if("2".equals(order.getStatus())){
            return 0;
        }
        if(ObjectUtil.isEmpty(order)){
            return 0;
        }

        // ??????????????????list
        List<SubEditLogisticsVo> list = dto.getLogisticList();

        // ??????????????????????????????????????????String
        String logisticFinal = null;

        // ??????????????????????????????
        String logistics = order.getLogistics();
        if(StringUtils.isNotEmpty(logistics)){
            // ??????????????????jsonStr
            JSONArray jsonArray = JSON.parseArray(logistics);
            List<SubEditLogisticsVo> editLogisticsVos= jsonArray.toJavaList(SubEditLogisticsVo.class);
            // ????????????editLogisticsVos  add???  ????????????list
            boolean b = list.addAll(editLogisticsVos);
            if(!b) return 0;
        }

        // ??????String, ??????????????????????????????
        logisticFinal = JSON.toJSONString(list);


        // if(ObjectUtils.isNull(logisticFinal)) return 0;

        order.setLogistics(logisticFinal);
        order.setStatus("1");
        int i = wspOrderMapper.updateById(order);
        return i==1? 1:0;
    }





    /**
     * ????????????
     *
     * @param wspOrderVO
     * @return
     */
    @Override
    public List<WspOrder> getOrderList(WspOrderVO wspOrderVO) {
        LambdaQueryWrapper<WspOrder> queryWrapper = new LambdaQueryWrapper<>();
        if (ObjectUtil.isNotEmpty(wspOrderVO)){
            queryWrapper  = queryParam(wspOrderVO, queryWrapper);
        }
        List<WspOrder> wspOrders = wspOrderMapper.selectList(queryWrapper);
        return wspOrders;
    }


    /**
     * ??????????????????
     * @param file
     * @return
     * @throws IOException
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result batchShipment(MultipartFile file) throws IOException {
        //??????excel????????????
        List<Object> list = ExcelUtil.readInputStreamMoreThan1000RowBySheet(file.getInputStream(),null);
        list.remove(0);
        List<WspOrderBatchImportEto> insertBatchList = new ArrayList<>(list.size());
        //???List<String>??????WspOrderETO
        list.stream().forEach((obj) -> {
            List<String> strs = (List<String>) obj;
            WspOrderBatchImportEto wspOrderETO = new WspOrderBatchImportEto();
            this.importInfoFormatData(strs, wspOrderETO);
            insertBatchList.add(wspOrderETO);
        });

        for (WspOrderBatchImportEto wspOrderETO : insertBatchList) {
            List<BatchImportVo> batchImportVos = new ArrayList<>();
            //??????????????????????????????????????????????????????????????????
            if(StringUtils.isNotEmpty(wspOrderETO.getCourierNumber() ) && StringUtils.isNotEmpty(wspOrderETO.getLogisticsCompany())){
                String courierNumber = wspOrderETO.getCourierNumber();
                System.out.println("courierNumber = " + courierNumber);
                String as1[] = null;
                if (courierNumber.contains(",")){
                    as1 = courierNumber.split(",");
                }else {
                    String as3[] = {courierNumber};
                    as1 = as3;
                }
                //???????????????????????????????????????????????????????????????
                String logisticsCompany = wspOrderETO.getLogisticsCompany();
                System.out.println("logisticsCompany = " + logisticsCompany);
                String as2[] = null;
                if (logisticsCompany.contains(",")){
                    as2 = logisticsCompany.split(",");
                }else {
                    String as3[] = {logisticsCompany};
                    as2 = as3;
                }
                //????????????????????????batchImportVos
                for (int i = 0; i < as1.length; i++) {
                    if(StringUtils.isNotBlank(as1[i]) || StringUtils.isNotBlank(as2[i])){
                        BatchImportVo batchImportVo = new BatchImportVo();
                        batchImportVo.setCourierNumber(as1[i]);
                        batchImportVo.setLogisticsCompany(as2[i]);
                        batchImportVos.add(batchImportVo);
                    }
                }
            }
            //??????????????????????????????
            WspOrderVO wspOrderVO = new WspOrderVO();
            wspOrderVO.setOrderNumber(wspOrderETO.getOrderNumber());
            List<WspOrder> orderList = getOrderList(wspOrderVO);
            if (ObjectUtil.isNotEmpty(orderList)){
               if(!"2".equals(orderList.get(0).getStatus())){
                   //?????????????????????????????????
                   if (ObjectUtil.isNotEmpty(orderList.get(0).getLogistics())){
                       List<BatchImportVo> batchImportVoList = JSONArray.parseArray(orderList.get(0).getLogistics(), BatchImportVo.class);
                       Boolean operate = false;
                       for (BatchImportVo batchImportVo : batchImportVos) {
                           if (!batchImportVoList.contains(batchImportVo)){
                               batchImportVoList.add(batchImportVo);
                               operate = true;
                           }
                       }
                       if (operate){
                           WspOrder wspOrder = orderList.get(0);
                           String s = JSON.toJSONString(batchImportVoList);
                           wspOrder.setLogistics(s);
                           updateByEntityId(wspOrder);
                       }
                   }else {
                       WspOrder order = orderList.get(0);
                       String s = JSON.toJSONString(batchImportVos);
                       order.setLogistics(s);
                       order.setStatus("1");
                       updateByEntityId(order);
                   }
               }
            }
        }
        return new Result(20000,"????????????");
    }

    @Override
    public boolean updateByEntityId(WspOrder dto) {
        return super.updateById(dto);
    }


    private void importInfoFormatData(List<String> strs, WspOrderBatchImportEto entity) {

        Mirror<WspOrderBatchImportEto> mirror = Mirror.me(entity);
        Field[] fields = mirror.getFields();
        for (Field field : fields) {
            Excel excelAnnotation = field.getAnnotation(Excel.class);
            if (excelAnnotation == null) {
                continue;
            }
            int columNum = excelAnnotation.columnNum();
            if (columNum == -1) {
                continue;
            }
            String val = strs.get(columNum);
            Type type = field.getType();
            if (type == String.class) {
                mirror.setValue(entity, field, val);
            } else if (type == Integer.class) {
                mirror.setValue(entity, field, Double.valueOf(val).intValue());
            } else if (type == Date.class) {
                mirror.setValue(entity, field, DateUtil.parse(val));
            }
        }
    }


    /**
     * ????????????
     *
     * @param wspOrderVO
     * @return
     */
    @Override
    public List<OrderQueryVo> getOrderListToken(WspOrderVO wspOrderVO, HttpServletRequest request) {

        LambdaQueryWrapper<WspOrder> queryWrapper = new LambdaQueryWrapper<>();
        if (ObjectUtil.isNotEmpty(wspOrderVO)){
            //????????????
            if (ObjectUtil.isNotEmpty(wspOrderVO.getOrderNumber())){
                queryWrapper.eq(WspOrder::getOrderNumber,wspOrderVO.getOrderNumber());
            }
            //????????????
            if (ObjectUtil.isNotEmpty(wspOrderVO.getStartTime())){
                queryWrapper.ge(WspOrder::getOrderTime,wspOrderVO.getStartTime());
            }
            //????????????
            if (ObjectUtil.isNotEmpty(wspOrderVO.getEndTime())){
                queryWrapper.le(WspOrder::getOrderTime,wspOrderVO.getEndTime());
            }
            //userId????????????token
            if (ObjectUtil.isNotEmpty(request.getHeader("token"))){
                //??????TOKEN??????userID
                String token = request.getHeader("token");

                String decrypt = EncryptorUtil.decrypt(token);
                AccessToken accessToken = JSONObject.parseObject(decrypt, AccessToken.class);
                String userId = accessToken.getUserId();
                queryWrapper.eq(WspOrder::getUserId,userId);
            }

            //status
            if (ObjectUtil.isNotEmpty(wspOrderVO.getStatus())){
                queryWrapper.eq(WspOrder::getStatus,wspOrderVO.getStatus());
            }
        }
        List<WspOrder> wspOrders = wspOrderMapper.selectList(queryWrapper);
        List<OrderQueryVo> orderQueryVos = new ArrayList<>();
        for (WspOrder wspOrder : wspOrders) {
            OrderQueryVo orderQueryVo = new OrderQueryVo();
            BeanUtils.copyProperties(wspOrder,orderQueryVo);
            ArrayList<OrderGoodsVo> userList  = JSON.parseObject(wspOrder.getGoodsList(), new TypeReference<ArrayList<OrderGoodsVo>>(){});
            orderQueryVo.setOrderGoods(userList);

            ArrayList<BatchImportVo> batchImportVos  = JSON.parseObject(wspOrder.getLogistics(), new TypeReference<ArrayList<BatchImportVo>>(){});
            orderQueryVo.setExpress(batchImportVos);
            orderQueryVos.add(orderQueryVo);
        }
        return orderQueryVos;
    }


    // ????????????id????????? ??????????????????
    @Override
    public List<WspOrder> queryOrderByUserId(String userId){
        LambdaQueryWrapper<WspOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WspOrder::getStatus, "0");
        queryWrapper.eq(WspOrder::getUserId,userId);
        List<WspOrder> orders = wspOrderMapper.selectList(queryWrapper);
        return orders;
    }


    /**
     * ?????????????????????
     *
     * @param wspOrderVO
     * @return
     */
    @Override
    public List<OrderQueryVo> getOrderPageList(WspOrderVO wspOrderVO) {
        LambdaQueryWrapper<WspOrder> queryWrapper = new LambdaQueryWrapper<>();
        if (ObjectUtil.isNotEmpty(wspOrderVO)){
            queryWrapper  = queryParam(wspOrderVO, queryWrapper);
        }
        //????????????????????????
        List<WspOrder> wspOrders = wspOrderMapper.selectList(queryWrapper);
        List<OrderQueryVo> orderQueryVos = new ArrayList<>();
        for (WspOrder record : wspOrders) {
            OrderQueryVo orderQueryVo = new OrderQueryVo();
            BeanUtils.copyProperties(record,orderQueryVo);

            // ?????????????????? --zzn
            WspUser user = wspUserService.getUserById(orderQueryVo.getUserId());
            if(ObjectUtils.isNotEmpty(user)){
                orderQueryVo.setComNum(user.getUserCom());
                orderQueryVo.setUserDept(user.getUserDept());
            }

            ArrayList<OrderGoodsVo> userList  = JSON.parseObject(record.getGoodsList(), new TypeReference<ArrayList<OrderGoodsVo>>(){});
            boolean result = false;
            if (ObjectUtil.isNotEmpty(wspOrderVO.getGoodsName()) || ObjectUtil.isNotEmpty(wspOrderVO.getGoodsType())){
                for (OrderGoodsVo orderGoodsVo : userList) {
                    if (ObjectUtil.isNotEmpty(wspOrderVO.getGoodsName())){
                        if (orderGoodsVo.getGoodsName().equals(wspOrderVO.getGoodsName())){
                            result = true;
                        }
                    }
                    if (ObjectUtil.isNotEmpty(wspOrderVO.getGoodsType())){
                        if (orderGoodsVo.getGoodsType().equals(wspOrderVO.getGoodsType())){
                            result = true;
                        }
                    }
                    if(result) break;
                }
                if (result){
                    orderQueryVo.setOrderGoods(userList);
                    ArrayList<BatchImportVo> batchImportVos  = JSON.parseObject(record.getLogistics(), new TypeReference<ArrayList<BatchImportVo>>(){});
                    orderQueryVo.setExpress(batchImportVos);
                    try {
                        WspGiftCard card = wspGiftCardService.getInfoById(orderQueryVo.getCardNumber() + "");
                        orderQueryVo.setGiftCardNumber(card.getGiftCardNum());
                    }catch (Exception e){

                    }
                    orderQueryVos.add(orderQueryVo);
                }
            }else {
                orderQueryVo.setOrderGoods(userList);
                ArrayList<BatchImportVo> batchImportVos  = JSON.parseObject(record.getLogistics(), new TypeReference<ArrayList<BatchImportVo>>(){});
                orderQueryVo.setExpress(batchImportVos);
                try {
                    WspGiftCard card = wspGiftCardService.getInfoById(orderQueryVo.getCardNumber() + "");
                    orderQueryVo.setGiftCardNumber(card.getGiftCardNum());
                }catch (Exception e){

                }
                orderQueryVos.add(orderQueryVo);
            }
        }
        return orderQueryVos;
    }


    /**
     * ?????????????????????-----------------------------------
     * */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Integer
    createOrder(WspOrderVO wspOrderVO, String userId) {

        // ????????????
        HashMap<String, Object> wallet = wspGiftCardService.queryCardByUserId(userId);
        if (ObjectUtil.isEmpty(wallet)) return 2;

        // ????????????
        if (ObjectUtil.isEmpty(wspOrderVO)) return 3;
        if (ObjectUtil.isEmpty(wspOrderVO.getOrderGoodsJson())) return 3;

        List<OrderGoodsVo> orderGoodsJson = wspOrderVO.getOrderGoodsJson();
        List<OrderGoodsVo> orderGoodsVoList = new ArrayList<>();

        // 3,1) ?????????????????? ????????????????????????????????????
        Object quotaMultipleObj = wallet.get("quotaMultiple");
        Integer quotaMultiple = Integer.valueOf(quotaMultipleObj.toString());

        // ????????????????????????????????????quotaMap -> HashMap<????????????code,??????>
        Object cardTypeQuotaListObj = wallet.get("typeQuotaVoList");
        List<Map<String, String>> cardTypeQuotaList = Object2ListMapUtil.castListMap(cardTypeQuotaListObj, String.class, String.class);
        Map<String,Integer> quotaMap = new HashMap<>();
        cardTypeQuotaList.forEach(pending->{
            quotaMap.put(pending.get("goodsType"), Integer.valueOf(pending.get("typeQuota")));
        });

        // ????????????
        HashMap<String, Integer> orderQuotaSum = new HashMap<>();
        for (OrderGoodsVo vo : orderGoodsJson) {

            // 3,1) ?????????????????? ????????????????????????????????????
            boolean b1 = demandChange.queryDemand(3, 1);
            if (b1) if (vo.getGoodsNum().compareTo(quotaMultiple) > 0) return 999;

            // ??????
            String goodsType = vo.getGoodsType();
            Integer quota = quotaMap.get(goodsType);
            if(ObjectUtil.isEmpty(quota)) quota = 0;

            if(vo.getGoodsNum()>0){
                // ????????????????????????
                Integer integer = orderQuotaSum.get(vo.getGoodsType());
                if(ObjectUtil.isEmpty(integer)) integer = 0;
                orderQuotaSum.put(vo.getGoodsType(), integer+vo.getGoodsNum());
                // ????????????
                if (integer+vo.getGoodsNum() > quota) return 2;
            }

            //?????????(????????????return 5
            boolean b = wspGoodsService.inventoryReduction(vo.getGoodsId(), vo.getGoodsNum());
            if (!b) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return 5;
            }

            //???????????????????????????????????????
            String s = wspGoodsPicService.queryCoverByGoodId(vo.getGoodsId());
            List<WspGoods> wspGoods = wspGoodsService.queryById(vo.getGoodsId());
            BeanUtils.copyProperties(wspGoods.get(0), vo);
            vo.setPicSavepath(s);
            orderGoodsVoList.add(vo);
        }

        String s = JSON.toJSONString(orderGoodsVoList);
        wspOrderVO.setGoodsList(s);
        WspOrder wspOrder = new WspOrder();
        BeanUtils.copyProperties(wspOrderVO, wspOrder);

        //??????????????????????????????
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        LambdaQueryWrapper<WspOrder> wspOrderLambdaQueryWrapper = new LambdaQueryWrapper<>();
        wspOrderLambdaQueryWrapper.eq(WspOrder::getOrderNumber, sdf.format(new Date()));
        List<WspOrder> wspOrders = wspOrderMapper.selectList(wspOrderLambdaQueryWrapper);
        //??????????????????????????????
        if (ObjectUtil.isNotEmpty(wspOrders)){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return 4;
        }

        // ????????????, ??????
        wspOrder.setOrderNumber(sdf.format(new Date()));
        wspOrder.setUserId(userId);
        wspOrder.setStatus("0");
        wspOrder.setOrderTime(format.format(new Date()));
        WspUser userById = wspUserService.getUserById(userId);
        wspOrder.setCardNumber(wallet.get("cardId").toString());

        if (ObjectUtil.isNotEmpty(userById) && StringUtils.isNotEmpty(userById.getUserPhone())) {
            wspOrder.setUserPhone(userById.getUserPhone());
        }
        if (ObjectUtil.isNotEmpty(userById) && StringUtils.isNotEmpty(userById.getUserCom())) {
            wspOrder.setComNum(userById.getUserCom());
        }
        boolean save = super.save(wspOrder);
        //???????????????????????????
        if(!save) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return 0;
        }
        wspGiftCardService.isUse(Integer.valueOf(wallet.get("cardId").toString()));
        return 1;

    }














    @Override
    public int quotaIsSpent(WspOrderVO wspOrderVO, String userId) {

        // ????????????
        HashMap<String, Object> wallet = wspGiftCardService.queryCardByUserId(userId);
        if (ObjectUtil.isEmpty(wallet)) return 2;

        // ????????????
        if (ObjectUtil.isEmpty(wspOrderVO)) return 3;
        if (ObjectUtil.isEmpty(wspOrderVO.getOrderGoodsJson())) return 3;

        List<OrderGoodsVo> orderGoodsJson = wspOrderVO.getOrderGoodsJson();
        List<OrderGoodsVo> orderGoodsVoList = new ArrayList<>();

        // 3,1) ?????????????????? ????????????????????????????????????
        Object quotaMultipleObj = wallet.get("quotaMultiple");
        Integer quotaMultiple = Integer.valueOf(quotaMultipleObj.toString());

        // ????????????????????????????????????quotaMap -> HashMap<????????????code,??????>
        Object cardTypeQuotaListObj = wallet.get("typeQuotaVoList");
        List<Map<String, String>> cardTypeQuotaList = Object2ListMapUtil.castListMap(cardTypeQuotaListObj, String.class, String.class);
        Map<String,Integer> quotaMap = new HashMap<>();
        cardTypeQuotaList.forEach(pending->{
            quotaMap.put(pending.get("goodsType"), Integer.valueOf(pending.get("typeQuota")));
        });

        // ????????????
        HashMap<String, Integer> orderQuotaSum = new HashMap<>();
        for (OrderGoodsVo vo : orderGoodsJson) {

            // 3,1) ?????????????????? ????????????????????????????????????
            boolean b1 = demandChange.queryDemand(3, 1);
            log.debug("b1---"+b1);
            if (b1) {//3.2????????????????????????
                log.debug("vo.getGoodsNum()----"+vo.getGoodsNum());
                log.debug("quotaMultiple----"+quotaMultiple);
                //3.3-??????????????????????????????????????????????????????
                if (vo.getGoodsNum().compareTo(quotaMultiple) > 0)
                    return 999;
            }

            // ??????
            String goodsType = vo.getGoodsType();
            Integer quota = quotaMap.get(goodsType);
            if(ObjectUtil.isEmpty(quota)) quota = 0;

            if(vo.getGoodsNum()>0){
                // ????????????????????????
                Integer integer = orderQuotaSum.get(vo.getGoodsType());
                if(ObjectUtil.isEmpty(integer)) integer = 0;
                orderQuotaSum.put(vo.getGoodsType(), vo.getGoodsNum()+integer);
                // ????????????
                if (vo.getGoodsNum()+integer > quota) return 2;
            }

        }
        // ??????map?????????key????????????
        for(Map.Entry<String, Integer> entry: orderQuotaSum.entrySet()){
            if(entry.getValue() < quotaMap.get(entry.getKey())) return 6;
        }
        return 888;

    }

    private LambdaQueryWrapper<WspOrder> queryParam( WspOrderVO wspOrderVO, LambdaQueryWrapper<WspOrder> queryWrapper){



        /** -------?????????????????? -----*/
        if(StringUtils.isNotEmpty(wspOrderVO.getComNum())){
            queryWrapper.eq(WspOrder::getComNum, wspOrderVO.getComNum());
        }
        if(StringUtils.isNotEmpty(wspOrderVO.getCardNumber())){
            queryWrapper.eq(WspOrder::getCardNumber, wspOrderVO.getCardNumber());
        }
        /**-------------------------*/



        //????????????
        if (ObjectUtil.isNotEmpty(wspOrderVO.getOrderNumber())){
            queryWrapper.eq(WspOrder::getOrderNumber,wspOrderVO.getOrderNumber());
        }
        //???????????????????????????
        if (ObjectUtil.isNotEmpty(wspOrderVO.getOrderName())){
            queryWrapper.like(WspOrder::getOrderName,wspOrderVO.getOrderName());
        }
        //????????????
        if (ObjectUtil.isNotEmpty(wspOrderVO.getStartTime())){
            queryWrapper.ge(WspOrder::getOrderTime,wspOrderVO.getStartTime());
        }
        //????????????
        if (ObjectUtil.isNotEmpty(wspOrderVO.getEndTime())){
            queryWrapper.le(WspOrder::getOrderTime,wspOrderVO.getEndTime());
        }
        //????????????
        if (ObjectUtil.isNotEmpty(wspOrderVO.getOrderTel())){
            queryWrapper.eq(WspOrder::getOrderTel,wspOrderVO.getOrderTel());
        }
        //orderId
        if (ObjectUtil.isNotEmpty(wspOrderVO.getOrderId())){
            queryWrapper.eq(WspOrder::getOrderId,wspOrderVO.getOrderId());
        }
        //status
        if (ObjectUtil.isNotEmpty(wspOrderVO.getStatus())){
            queryWrapper.eq(WspOrder::getStatus,wspOrderVO.getStatus());
        }
        // ???????????????
        if (StringUtils.isNotEmpty(wspOrderVO.getUserPhone())){
            queryWrapper.eq(WspOrder::getUserPhone, wspOrderVO.getUserPhone());
        }
        return queryWrapper;
    }

    public static void main(String[] args) {
            //??????TOKEN??????userID
            String token  = "gD41qv1W0u/epw8VRoYCce74PcRikusZKlGhpaeeZlKIkrlxP5P23Q==";

            String decrypt = EncryptorUtil.decrypt(token);
            AccessToken accessToken = JSONObject.parseObject(decrypt, AccessToken.class);
            String userId = accessToken.getUserId();
        System.out.println("userId = " + userId);
    }

}
