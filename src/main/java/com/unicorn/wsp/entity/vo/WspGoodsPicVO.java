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
public class WspGoodsPicVO extends BasePageDto{


    @ApiModelProperty(value="id")
    private String picId;


    @ApiModelProperty(value="存储路径")
    private String picSavepath;


    @ApiModelProperty(value="图片原名")
    private String picRealname;


    @ApiModelProperty(value="是否为封面")
    private String isCover;


    @ApiModelProperty(value="图片顺序")
    private String picSort;


    @ApiModelProperty(value="商品id")
    private String goodsId;


}