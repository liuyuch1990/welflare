package com.unicorn.wsp.utils;

import com.alibaba.fastjson.JSONObject;
import com.unicorn.wsp.entity.WspUser;
import com.unicorn.wsp.vo.AccessToken;

import javax.servlet.http.HttpServletRequest;

public class TokenUtils {

    // 封装token
    public static String packageHeader(WspUser user){
        AccessToken accessToken = new AccessToken(user.getUserId(), user.getUserPhone(), user.getUserCom());
        String s = JSONObject.toJSONString(accessToken);
        return EncryptorUtil.encrypt(JSONObject.toJSONString(accessToken));
    }

    // 分析token
    public static AccessToken analyseRequest(HttpServletRequest request){
        String token = request.getHeader("token");
        String decrypt = EncryptorUtil.decrypt(token);
        AccessToken accessToken = JSONObject.parseObject(decrypt, AccessToken.class);
        return accessToken;
    }

}
