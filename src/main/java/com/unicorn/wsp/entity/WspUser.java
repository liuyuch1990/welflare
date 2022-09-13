package com.unicorn.wsp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import com.unicorn.wsp.common.annotation.Excel;
import com.unicorn.wsp.common.base.BaseEntity;

/**
* 
*
* @Author spark
* @Version 1.0
*/
@Data
@Builder
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "wsp_user", autoResultMap = true)
public class WspUser extends BaseEntity {


    @TableId(value = "user_id",type = IdType.INPUT)
    @Excel(columnNum = 0)
    private String userId;

    @Excel(columnNum = 1)
    private String userCom;

    @Excel(columnNum = 2)
    private String userNo;

    @Excel(columnNum = 3)
    private String userName;

    @Excel(columnNum = 4)
    private String userDept;

    @Excel(columnNum = 5)
    private String userPhone;

    @Excel(columnNum = 6)
    private String userPwd;

    @Excel(columnNum = 7)
    private String isDisable;



}