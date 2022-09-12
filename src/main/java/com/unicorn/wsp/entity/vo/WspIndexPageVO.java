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
public class WspIndexPageVO extends BasePageDto{


    @ApiModelProperty(value="id")
    private String indexId;


    @ApiModelProperty(value="首页文字")
    private String indexText;


    @ApiModelProperty(value="登录图片")
    private String loginPic;


    @ApiModelProperty(value="首页图片1")
    private String indexPic1;


    @ApiModelProperty(value="首页图片2")
    private String indexPic2;


    @ApiModelProperty(value="首页图片3")
    private String indexPic3;


    @ApiModelProperty(value="首页图片4")
    private String indexPic4;


    @ApiModelProperty(value="首页图片5")
    private String indexPic5;


    @ApiModelProperty(value="公司码")
    private String comNum;


}