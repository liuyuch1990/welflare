package com.unicorn.wsp.entity.exportvo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
* 
*
* @Author spark
* @Version 1.0
*/
@Data
@EqualsAndHashCode(callSuper = false)
public class WspAddrETO {

    /**
    * id
    */
    private Integer addrId;

    /**
    * 地址名
    */
    private String addrName;

    /**
    * 收货人
    */
    private String receiverName;

    /**
    * 收货人手机号码
    */
    private String receiverPhone;

    /**
    * 省号
    */
    private String provinceCode;

    /**
    * 市号
    */
    private String cityCode;

    /**
    * 区号
    */
    private String areaCode;

    /**
    * 省市区拼接字符串
    */
    private String areaName;

    /**
    * 详细地址
    */
    private String addrContent;

    /**
    * 是否默认
    */
    private String isDef;

    /**
    * 用户id(外键)
    */
    private String userId;



}