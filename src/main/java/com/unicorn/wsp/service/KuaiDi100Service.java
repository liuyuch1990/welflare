
package com.unicorn.wsp.service;

import com.alibaba.fastjson.JSONObject;
import com.unicorn.wsp.common.result.Result;
import com.unicorn.wsp.entity.KuaiDiCode;
import com.unicorn.wsp.utils.R;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * 快递100 相关操作
 * @author spark
 * @version 1.0.0
 */
@Service
public interface KuaiDi100Service {



/**
     * 获取快递信息
     * @param orderId
     */

    public JSONObject findOrder(String orderId) throws Exception;


/**
     * 获取快递公司编号信息
     * @param orderId
     * @return
     */

    public List<KuaiDiCode> findKuaiDiCode(String orderId);
}


