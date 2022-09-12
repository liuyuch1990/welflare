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
@TableName(value = "wsp_card_type", autoResultMap = true)
public class WspCardType extends BaseEntity {

    @TableId(value = "id", type = IdType.INPUT)
    @Excel(columnNum = 2)
    private String id;

    @Excel(columnNum = 0)
    private String cardTypeName;

    @Excel(columnNum = 1)
    private String comNum;





}