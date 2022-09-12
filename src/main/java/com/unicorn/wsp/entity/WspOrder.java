package com.unicorn.wsp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.unicorn.wsp.vo.OrderListVo;
import com.unicorn.wsp.vo.OrderQueryVo;
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
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "wsp_order", autoResultMap = true)
public class WspOrder {


    @Excel
    @TableId(value = "order_id", type = IdType.INPUT)
    private Integer orderId;

    @Excel(columnNum = 1)
    private String userId;

    @Excel(columnNum = 2)
    private String orderName;

    @Excel(columnNum = 3)
    private String orderTel;

    @Excel(columnNum = 4)
    private String orderAddress;

    @Excel(columnNum = 5)
    private String status;

    @Excel(columnNum = 6)
    private String goodsList;

    @Excel(columnNum = 7)
    private String orderTime;

    @Excel(columnNum = 8)
    private String orderNumber;

    @Excel(columnNum = 9)
    private String deliverGoodsTime;

    @Excel(columnNum = 10)
    private String updateTime;

    @Excel(columnNum = 11)
    private Integer isDel;

    @Excel(columnNum = 12)
    private String sort;

    @Excel(columnNum = 13)
    private String logistics;

    @Excel(columnNum = 14)
    private String userPhone;

    @Excel(columnNum = 15)
    private String cardNumber;

    @Excel(columnNum = 16)
    private String comNum;



}