package com.unicorn.wsp.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.unicorn.wsp.common.annotation.Excel;
import com.unicorn.wsp.entity.vo.BatchImportVo;
import lombok.Data;

import java.util.List;

/**
 * @author spark
 * @date 2021/11/10
 */
@Data
public class OrderQueryVo extends OrderListVo{
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
    private String userPhone;

    @Excel(columnNum = 14)
    private String cardNumber;

    @Excel(columnNum = 15)
    private String comNum;

    @Excel(columnNum = 16)
    private String giftCardNumber;
}
