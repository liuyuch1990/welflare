package com.unicorn.wsp.utils;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.kuaidi100.sdk.api.AutoNum;
import com.kuaidi100.sdk.api.QueryTrack;
import com.kuaidi100.sdk.core.IBaseClient;
import com.kuaidi100.sdk.request.AutoNumReq;
import com.kuaidi100.sdk.request.QueryTrackParam;
import com.kuaidi100.sdk.request.QueryTrackReq;
import com.kuaidi100.sdk.utils.SignUtils;

/**
 * @Author: spark
 */
public class KuaidiUtils {

    private static String key = "GBrbDdlA6859";
    private static String customer = "5527711A0C1163813D654378B204FBA5";

    public static String queryTrack(String num,String phone) throws Exception {

        QueryTrackReq queryTrackReq = new QueryTrackReq();
        QueryTrackParam queryTrackParam = new QueryTrackParam();
        JSONArray obj = JSONArray.parseArray(autoNum(num));
        queryTrackParam.setCom(obj.getJSONObject(0).getString("comCode"));
        queryTrackParam.setNum(num);
        if (ObjectUtil.isNotEmpty(phone)) {
            queryTrackParam.setPhone(phone);
        }

        String param = JSON.toJSONString(queryTrackParam);

        queryTrackReq.setParam(param);
        queryTrackReq.setCustomer(customer);
        queryTrackReq.setSign(SignUtils.querySign(param, key, customer));

        IBaseClient baseClient = new QueryTrack();
        return baseClient.execute(queryTrackReq).getBody();
    }

    public static String autoNum(String num) throws Exception{
        AutoNumReq autoNumReq = new AutoNumReq();
        autoNumReq.setKey(key);
        autoNumReq.setNum(num);

        IBaseClient baseClient = new AutoNum();
        System.out.println();
        return baseClient.execute(autoNumReq).getBody();
    }

    public static void main(String[] args) throws Exception {
//        KuaidiUtils.autoNum("75804461891496");
        System.out.println(KuaidiUtils.queryTrack("75804461891496","15130348585"));
    }
}
