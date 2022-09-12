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
public class SimpleDictVO extends BasePageDto{


    @ApiModelProperty(value="字典标识")
    private String dictCode;


    @ApiModelProperty(value="字典名")
    private String dictName;


    @ApiModelProperty(value="分类")
    private String dictType;


    @ApiModelProperty(value="")
    private Integer id;


}