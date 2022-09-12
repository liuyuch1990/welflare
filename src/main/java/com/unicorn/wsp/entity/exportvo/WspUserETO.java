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
public class WspUserETO {

    /**
    * id
    */
    private String userId;

    /**
    * 公司编号
    */
    private String userCom;

    /**
    * 工号
    */
    private String userNo;

    /**
    * 姓名
    */
    private String userName;

    /**
    * 手机号
    */
    private String userPhone;

    /**
    * 密码
    */
    private String userPwd;

    private String isDisable;



}