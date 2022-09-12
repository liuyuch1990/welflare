package com.unicorn.wsp.entity.exportvo;

import io.swagger.annotations.ApiModelProperty;
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
public class WspCardQuotaETO {

    // id
    private Integer quotaId;


    // 礼品卡类型id
    private String cardTypeId;


    // 额度
    private Integer typeQuota;


    // 商品种类
    private String goodsType;


    private String isDel;


}