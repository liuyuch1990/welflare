package com.unicorn.wsp.utils;


import com.alibaba.fastjson.JSON;
import com.kuaidi100.sdk.utils.SignUtils;
import com.unicorn.wsp.entity.KuaiDiCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 顺丰快递 Api 接口对接， 官网文档-零担下单： https://freight.sf-express.com/api/api.html#id=30
 * <P>
 * 请求头必须添加： "Content-type","application/x-www-form-urlencoded;charset=UTF-8”
 *
 * </P>
 * @author wangsong
 * @mail 1720696548@qq.com
 * @date 2020/9/9 0009 9:38
 * @version 1.0.0
 */

@SuppressWarnings("all")
@Component
@Slf4j
public class KuaiDi100Util {

    @Autowired
    private RestTemplate restTemplate;

/**
     * 智能识别接口
     * NUM =单号
     * KEY =授权码
     */

    private static final String AUTONUMBER_AUTO_URL = "http://www.kuaidi100.com/autonumber/auto?num=NUM&key=KEY";

/**
     * 快递100查询快递请求接口（post）
     */

    private static final String POLL_QUERY_URL = "https://poll.kuaidi100.com/poll/query.do";


    // 授权码,授权码，请到快递100页面申请企业版接口获取
    private static final String KEY = "GBrbDdlA6859";
    private static final String CUSTOMER = "5527711A0C1163813D654378B204FBA5";
//    private static final String SIGN = ;
    // sign  签名， 用于验证身份， 按param + key + customer 的顺序进行MD5加密（注意加密后字符串一定要转大写）， 不需要加上“+”号



/**
     * 快递100智能识别快递编号
     * @author wangsong
     * @author requestCreate 下单参数
     * @date 2020/9/15 0015 15:42
     * @return void
     * @version 1.0.0
     */

    public List<KuaiDiCode> findKuaiDiCode(String orderId) {
        String url = AUTONUMBER_AUTO_URL.replace("NUM", orderId).replace("KEY", KEY);
        // 发送快递参数处理
        MultiValueMap<String, Object> sendBody = new LinkedMultiValueMap<>();
        sendBody.add("num", orderId);
        sendBody.add("key", KEY);
        //设置请求头参数
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded");
        HttpEntity<MultiValueMap<String, Object>> formEntity = new HttpEntity<>(sendBody, headers);
        ResponseEntity<String> result = restTemplate.postForEntity(url, formEntity, String.class);
        List<KuaiDiCode> kuaiDiCode = new ArrayList<>();
        if (result.getBody() != null && result.getBody().length() > 0) {
            ArrayList body = JSON.parseObject(result.getBody(), ArrayList.class);
            body.forEach(i -> kuaiDiCode.add(JSON.parseObject(JSON.toJSONString(i), KuaiDiCode.class)));
        }
        return kuaiDiCode;
    }



/**
     *  快递100 物流查询
     * @param com 快递公司编码
     * @param num 快递单号
     * @return SFReturnData
     */

    public String findOrder(String num, String com) {
        Map<String, String> param = new HashMap<>();
        param.put("com", com);//快递公司编码
        param.put("num", num);//快递单号
        //
        String mgsData = JSON.toJSONString(param);
        // 发送快递参数处理
        MultiValueMap<String, Object> sendBody = new LinkedMultiValueMap<>();
        sendBody.add("customer", CUSTOMER);                          // 授权码，请到快递100页面申请企业版接口获取
        sendBody.add("sign", SignUtils.querySign(mgsData, KEY, CUSTOMER));    // 数字签名
        sendBody.add("param", mgsData);                            // 业务数据报文
        //设置请求头参数
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded");
        //发送请求
        HttpEntity<MultiValueMap<String, Object>> formEntity = new HttpEntity<>(sendBody, headers);
        ResponseEntity<String> result = restTemplate.postForEntity(POLL_QUERY_URL, formEntity, String.class);
        //
        // SFReturnData sfReturnData = JSON.parseObject(result.getBody(), SFReturnData.class);
        return result.getBody();
    }



/**
     * 业务数据加密  -->   param + key + customer 的顺序进行MD5加密（注意加密后字符串一定要转大写）
     * @param timestamp
     * @param mgsData
     * @param md5key
     * @return
     * @throws Exception
     */

    private String genDigest(String mgsData, String key, String customer) {
        return DigestUtils.md5Hex(mgsData + key + customer).toUpperCase();
    }
}


