package com.unicorn.wsp.entity.exportvo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
* 
*
* @Author spark
* @Version 1.0
*/
@Data
@EqualsAndHashCode(callSuper = false)
public class WspGoodsPicETO {

    /**
    * id
    */
    private String picId;

    /**
    * 存储路径
    */
    private String picSavepath;

    /**
    * 图片原名
    */
    private String picRealname;

    /**
    * 是否为封面
    */
    private String isCover;

    /**
    * 图片顺序
    */
    private String picSort;

    /**
    * 商品id
    */
    private String goodsId;



}