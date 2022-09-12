package com.unicorn.wsp.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import com.unicorn.wsp.common.base.BasePageDto;

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
public class WspGoodsVO extends BasePageDto{


    @ApiModelProperty(value="id")
    private String goodsId;


    @ApiModelProperty(value="商品名")
    private String goodsName;


    @ApiModelProperty(value="商品分类")
    private String goodsType;


    @ApiModelProperty(value="价格")
    private Float goodsPrice;


    @ApiModelProperty(value="库存")
    private Integer goodsSum;


    @ApiModelProperty(value="商品详情")
    private String goodsContent;


    @ApiModelProperty(value="上下架")
    private String isDel;

    @ApiModelProperty(value="销量")
    private Integer goodsSales;
    @ApiModelProperty(value="上架时间")
    private String createdDate;



}