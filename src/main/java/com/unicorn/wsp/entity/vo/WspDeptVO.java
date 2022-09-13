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
public class WspDeptVO extends BasePageDto{


    @ApiModelProperty(value="")
    private Integer id;


    @ApiModelProperty(value="部门名称")
    private String departmentName;


    @ApiModelProperty(value="公司名")
    private String companyName;

}