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
public class WspCardQuotaVO extends BasePageDto{


    @ApiModelProperty(value="id")
    private Integer quotaId;


    @ApiModelProperty(value="礼品卡类型id")
    private String cardTypeId;


    @ApiModelProperty(value="额度")
    private Integer typeQuota;


    @ApiModelProperty(value="商品种类")
    private String goodsType;


    @ApiModelProperty(value="")
    private String isDel;


}