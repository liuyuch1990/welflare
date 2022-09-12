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
public class WspGiftCardVO extends BasePageDto{


    @ApiModelProperty(value="id")
    private Integer giftCardId;


    @ApiModelProperty(value="礼品卡编号（兑换码）")
    private String giftCardNum;


    @ApiModelProperty(value="礼品卡名")
    private String cardTypeId;


    @ApiModelProperty(value="额度倍数")
    private Integer quotaMultiple;


    @ApiModelProperty(value="有效期")
    private String validDate;


    @ApiModelProperty(value="是否有效")
    private String isTrue;


    @ApiModelProperty(value="兑换状态（未绑定0，未兑换1，已兑换2）")
    private String isUse;


    @ApiModelProperty(value="绑定人")
    private String userId;

    @ApiModelProperty(value="绑定人手机号")
    private String userPhone;


    @ApiModelProperty(value="公司编号")
    private String comNum;


    @ApiModelProperty(value="是否删除")
    private String isDel;


}