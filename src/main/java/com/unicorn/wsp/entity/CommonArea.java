package com.unicorn.wsp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import com.unicorn.wsp.common.annotation.Excel;
import com.unicorn.wsp.common.base.BaseEntity;

/**
* 
*
* @Author spark
* @Version 1.0
*/
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "common_area", autoResultMap = true)
public class CommonArea extends BaseEntity {


    @Excel(columnNum = 0)
    private Integer id;

    @Excel(columnNum = 1)
    private String code;

    @Excel(columnNum = 2)
    private String name;

    @Excel(columnNum = 3)
    private String citycode;



}