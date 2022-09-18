package com.unicorn.wsp.entity.vo;

import com.unicorn.wsp.common.annotation.Excel;
import com.unicorn.wsp.vo.OrderGoodsVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import com.unicorn.wsp.common.base.BasePageDto;

import java.util.List;

/**
* 
*
* @Author spark
* @Version 1.0
*/
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="",description="")
public class WspOrderVO extends BasePageDto{


    @ApiModelProperty(value="主键")
    private Integer orderId;


    @ApiModelProperty(value="用户id")
    private String userId;


    @ApiModelProperty(value="下单姓名")
    private String orderName;


    @ApiModelProperty(value="下单电话")
    private String orderTel;


    @ApiModelProperty(value="订单地址")
    private String orderAddress;


    @ApiModelProperty(value="订单状态")
    private String status;


    @ApiModelProperty(value="下单商品json")
    private String goodsList;


    @ApiModelProperty(value="下单时间")
    private String orderTime;


    @ApiModelProperty(value="订单编号")
    private String orderNumber;


    @ApiModelProperty(value="发货时间")
    private String deliverGoodsTime;


    @ApiModelProperty(value="更新时间")
    private String updateTime;


    @ApiModelProperty(value="是否删除")
    private Integer isDel;

    @ApiModelProperty(value="商品种类")
    private String sort;

    @ApiModelProperty(value="物流json")
    private String logistics;

    private String userPhone;

    @ApiModelProperty(value="下单商品查询json")
    private List<OrderGoodsVo> orderGoodsJson;

    private String goodsName;

    private String goodsType;

    private String cardNumber;

    private String comNum;

    private String stars;

    private String comment;

    private String rollReason;

    private String rollPics;

    private String appointment;

    private String userNo;

    private String userName;

}