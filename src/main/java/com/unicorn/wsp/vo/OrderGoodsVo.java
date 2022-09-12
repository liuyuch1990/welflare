package com.unicorn.wsp.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author spark
 * @date 2021/10/31
 */
@Data
public class OrderGoodsVo {

    private String goodsId;

    private String goodsName;

    private String goodsType;

    private String goodsPrice;

    private String goodsContent;

    private Integer goodsNum;

    private String picSavepath;
}
