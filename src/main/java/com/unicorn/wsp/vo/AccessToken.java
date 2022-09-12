package com.unicorn.wsp.vo;

import io.swagger.models.auth.In;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AccessToken {

    // 用户名(唯一標識)
    private String phone;

    private String userId;

    private String comNum;

    private int roleId;


    // 过期时间
    private long expire;

    public AccessToken(String phone){
        this.phone = phone;

        // 设置24h过期
        this.expire = System.currentTimeMillis()+1000L*3600*24;
    }

    public AccessToken(String id, String phone){
        this.phone = phone;
        this.userId = id;

        // 设置24h过期
        this.expire = System.currentTimeMillis()+1000L*3600*24;
    }

    public AccessToken(String id, String phone, String comNum){
        this.phone = phone;
        this.userId = id;
        this.comNum = comNum;

        // 设置两分钟过期
        this.expire = System.currentTimeMillis()+1000L*3600*24;
    }

    public AccessToken(String id, String phone, String comNum, int roleId){
        this.phone = phone;
        this.userId = id;
        this.comNum = comNum;
        this.roleId = roleId;

        // 设置两分钟过期
        this.expire = System.currentTimeMillis()+1000L*3600*24       *100;
    }

}
