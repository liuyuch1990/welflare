package com.unicorn.wsp.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author spark
 * @date 2021/11/17
 */
@Data
public class OrderQueryPageDto {

    private List<OrderQueryVo> orderQueryVoList;

    private Integer pageNum;

    private Integer pageSize;

    private Integer total;
}