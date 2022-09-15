package com.unicorn.wsp.entity.vo;

import com.unicorn.wsp.common.base.BasePageDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

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
public class WspSwiperVO extends BasePageDto{


    @ApiModelProperty(value="id")
    private String id;


    @ApiModelProperty(value="名称")
    private String image;


    @ApiModelProperty(value="路径")
    private String imagePath;


    @ApiModelProperty(value="排序")
    private Integer sort;



}