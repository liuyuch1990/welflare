
package com.unicorn.wsp.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import com.unicorn.wsp.common.result.Result;
import com.unicorn.wsp.entity.KuaiDiCode;
import com.unicorn.wsp.service.KuaiDi100Service;
import com.unicorn.wsp.utils.KuaiDi100Util;
import com.unicorn.wsp.utils.KuaidiUtils;
import com.unicorn.wsp.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * 快递100
 * @author spark
 * @version 1.0.0
 */

@Service
public class KuaiDi100ServiceImpl implements KuaiDi100Service {

    @Autowired
    private KuaiDi100Util kuaiDi100Util;


    @Override
    public JSONObject findOrder(String orderId) throws Exception {
        //获取快递编码
        String res = KuaidiUtils.queryTrack(orderId,null);
        JSONObject object = JSONObject.parseObject(res);
        return object;
    }

    @Override
    public List<KuaiDiCode> findKuaiDiCode(String orderId) {
        List<KuaiDiCode> kuaiDiCode = kuaiDi100Util.findKuaiDiCode(orderId);
        return kuaiDiCode;
    }
}


