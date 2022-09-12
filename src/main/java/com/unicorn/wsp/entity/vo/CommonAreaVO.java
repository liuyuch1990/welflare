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
public class CommonAreaVO extends BasePageDto{


    @ApiModelProperty(value="")
    private Integer id;


    @ApiModelProperty(value="")
    private String code;


    @ApiModelProperty(value="")
    private String name;


    @ApiModelProperty(value="")
    private String citycode;


}