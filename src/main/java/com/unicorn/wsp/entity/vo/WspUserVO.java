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
public class WspUserVO extends BasePageDto{


    @ApiModelProperty(value="id")
    private String userId;


    @ApiModelProperty(value="公司编号")
    private String userCom;


    @ApiModelProperty(value="工号")
    private String userNo;


    @ApiModelProperty(value="姓名")
    private String userName;


    @ApiModelProperty(value="手机号")
    private String userPhone;

    @ApiModelProperty(value="体系/部门")
    private String userDept;


    @ApiModelProperty(value="密码")
    private String userPwd;

    @ApiModelProperty(value="是否禁用")
    private String isDisable;


}