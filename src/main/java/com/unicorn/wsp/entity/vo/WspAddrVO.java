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
public class WspAddrVO extends BasePageDto{


    @ApiModelProperty(value="id")
    private Integer addrId;


    @ApiModelProperty(value="地址名")
    private String addrName;


    @ApiModelProperty(value="收货人")
    private String receiverName;


    @ApiModelProperty(value="收货人手机号码")
    private String receiverPhone;


    @ApiModelProperty(value="省号")
    private String provinceCode;


    @ApiModelProperty(value="市号")
    private String cityCode;


    @ApiModelProperty(value="区号")
    private String areaCode;


    @ApiModelProperty(value="省市区拼接字符串")
    private String areaName;


    @ApiModelProperty(value="详细地址")
    private String addrContent;


    @ApiModelProperty(value="是否默认")
    private String isDef;


    @ApiModelProperty(value="用户id(外键)")
    private String userId;


}