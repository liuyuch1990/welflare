package com.unicorn.wsp.entity.zznvo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelTarget;
import lombok.Data;

import java.io.Serializable;

@Data
@ExcelTarget("20")
public class UserPoi implements Serializable {

    /**
     * id
     */
    @Excel(name="id",width = 20, orderNum = "0")
    private String userId;

    /**
     * 公司编号
     */
    @Excel(name="公司编号",width = 20, orderNum = "1")
    private String userCom;

    /**
     * 工号
     */
    @Excel(name="工号",width = 20, orderNum = "2")
    private String userNo;

    /**
     * 姓名
     */
    @Excel(name="姓名",width = 20, orderNum = "3")
    private String userName;

    /**
     * 手机号
     */
    @Excel(name="手机号",width = 20, orderNum = "4")
    private String userPhone;

    /**
     * 密码
     */
    @Excel(name="密码",width = 20, orderNum = "5")
    private String userPwd;

    @Excel(name="是否禁用",width = 20, orderNum = "6")
    private String isDisable;
}
