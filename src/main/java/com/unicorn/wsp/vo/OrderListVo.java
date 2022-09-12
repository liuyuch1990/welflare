package com.unicorn.wsp.vo;

import com.unicorn.wsp.common.annotation.Excel;
import com.unicorn.wsp.entity.vo.BatchImportVo;
import lombok.Data;

import java.util.List;

/**
 * @author spark
 * @date 2021/11/10
 */
@Data
public class OrderListVo {

    private List<OrderGoodsVo> orderGoods;

    private List<BatchImportVo> express;

}
