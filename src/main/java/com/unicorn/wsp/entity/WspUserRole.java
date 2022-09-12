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
@Builder
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "wsp_user_role", autoResultMap = true)
public class WspUserRole extends BaseEntity {


    @Excel(columnNum = 0)
    private Integer id;

    @Excel(columnNum = 1)
    private String userId;

    @Excel(columnNum = 2)
    private Integer roleId;



}