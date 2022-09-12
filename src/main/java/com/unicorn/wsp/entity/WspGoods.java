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
@TableName(value = "wsp_goods", autoResultMap = true)
public class WspGoods extends BaseEntity {


    @Excel(columnNum = 0)
    @TableId(value = "goods_id", type = IdType.INPUT)//非自增
    private String goodsId;

    @Excel(columnNum = 1)
    private String goodsName;

    @Excel(columnNum = 2)
    private String goodsType;

    @Excel(columnNum = 3)
    private Float goodsPrice;

    @Excel(columnNum = 4)
    private Integer goodsSum;

    @Excel(columnNum = 5)
    private String goodsContent;

    @Excel(columnNum = 6)
    private String isDel;

    @Excel(columnNum = 7)
    private Integer goodsSales;
    @Excel(columnNum = 8)
    private String createdDate;



}