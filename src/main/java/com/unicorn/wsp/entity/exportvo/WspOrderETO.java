package com.unicorn.wsp.entity.exportvo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import com.unicorn.wsp.common.annotation.Excel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
* 
*
* @Author spark
* @Version 1.0
*/
@Data
@EqualsAndHashCode(callSuper = false)
public class WspOrderETO extends BaseRowModel implements Serializable {


    /**
     * 订单编号
     */
    @ExcelProperty(value = "订单编号",index = 0)
    @Excel(columnNum = 0)
    private String orderNumber;
    /**
    * 下单姓名
    */
    @ExcelProperty(value = "收货人",index = 1)
    @Excel(columnNum = 1)
    private String orderName;

    /**
    * 下单电话
    */
    @ExcelProperty(value = "收货电话",index = 2)
    @Excel(columnNum = 2)
    private String orderTel;

    /**
    * 订单地址
    */
    @ExcelProperty(value = "订单地址",index = 3)
    @Excel(columnNum = 3)
    private String orderAddress;

    /**
    * 下单时间
    */
    @ExcelProperty(value = "下单时间",index = 4)
    @Excel(columnNum = 4)
    private String orderTime;

    /**
     * 快递单号
     */
    @ExcelProperty(value = "快递单号",index = 5)
    @Excel(columnNum = 5)
    private String courierNumber;

    /**
     * 物流公司
     */
    @ExcelProperty(value = "物流公司",index = 6)
    @Excel(columnNum = 6)
    private String logisticsCompany;



}