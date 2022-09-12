package com.unicorn.wsp.entity.exportvo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import com.unicorn.wsp.common.annotation.Excel;
import com.unicorn.wsp.config.ChangeTextConverter;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author spark
 * @date 2021/11/25
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class WspOrderBatchImportEto extends BaseRowModel implements Serializable {

    /**
     * 订单编号
     */
    @ExcelProperty(value = "订单编号",index = 0)
    @Excel(columnNum = 0)
    private String orderNumber;

    /**
     * 商品名称
     */
    @ExcelProperty(value = "商品",index = 1)
    @Excel(columnNum = 1)
    private String goodsName;


    @ExcelProperty(value = "数量",index = 2)
    @Excel(columnNum = 2)
    private Integer goodsNum;
    /**
     * 下单姓名
     */
    @ExcelProperty(value = "收货人姓名",index = 3)
    @Excel(columnNum = 3)
    private String orderName;

    /**
     * 下单电话
     */
    @ExcelProperty(value = "收货人电话",index = 4)
    @Excel(columnNum = 4)
    private String orderTel;

    /**
     * 订单地址
     */
    @ExcelProperty(value = "收货地址",index = 5)
    @Excel(columnNum = 5)
    private String orderAddress;

    /**
     * 下单时间
     */
    @ExcelProperty(value = "下单时间",index = 6)
    @Excel(columnNum = 6)
    private String orderTime;

    /**
     * 快递单号
     */
    @ExcelProperty(value = "快递单号",index = 7/*, converter = ChangeTextConverter.class*/)
    @Excel(columnNum = 7)
    private String courierNumber;

    /**
     * 物流公司
     */
    @ExcelProperty(value = "物流公司",index = 8)
    @Excel(columnNum = 8)
    private String logisticsCompany;

    @ExcelProperty(value = "公司编号", index = 9)
    @Excel(columnNum = 9)
    private String comNum;


    @ExcelProperty(value = "提货码", index = 10)
    @Excel(columnNum = 10)
    private String giftCardNumber;

    @ExcelProperty(value = "手机号", index = 11)
    @Excel(columnNum = 11)
    private String userPhone;

}
