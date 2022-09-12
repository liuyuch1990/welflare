package com.unicorn.wsp.entity.exportvo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
* 
*
* @Author spark
* @Version 1.0
*/
@Data
@EqualsAndHashCode(callSuper = false)
public class WspGoodsETO {

    /**
    * id
    */
    private String goodsId;

    /**
    * 商品名
    */
    private String goodsName;

    /**
    * 商品分类
    */
    private String goodsType;

    /**
    * 价格
    */
    private Float goodsPrice;

    /**
    * 库存
    */
    private Integer goodsSum;

    /**
    * 商品详情
    */
    private String goodsContent;

    /**
    * 上下架
    */
    private String isDel;

    private Integer goodsSales;
    private String createdDate;



}