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
public class WspComVO extends BasePageDto{


    @ApiModelProperty(value="")
    private Integer id;


    @ApiModelProperty(value="公司编号")
    private String comNum;


    @ApiModelProperty(value="公司名")
    private String comName;

    private String goodsTypeCode;



}