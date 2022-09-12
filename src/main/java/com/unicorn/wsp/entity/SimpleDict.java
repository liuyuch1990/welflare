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

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

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
@TableName(value = "simple_dict", autoResultMap = true)
public class SimpleDict extends BaseEntity {



    @Excel(columnNum = 0)
    @NotEmpty(message = "code不能为空")
    private String dictCode;

    @Excel(columnNum = 1)
    @NotEmpty(message = "name不能为空")
    private String dictName;

    @Excel(columnNum = 2)
    private String dictType;

    @TableId("id")
    @Excel(columnNum = 3)
    private Integer id;



}