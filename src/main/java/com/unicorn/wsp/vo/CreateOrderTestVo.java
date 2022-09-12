package com.unicorn.wsp.vo;

import com.unicorn.wsp.entity.zznvo.GiftCardVo;
import io.swagger.models.auth.In;
import lombok.Data;

/**
 * @author spark
 * @date 2021/11/16
 */
@Data
public class CreateOrderTestVo extends GiftCardVo {

    private Integer createOrderQuotaA;

    private Integer createOrderQuotaB;

    private Integer createOrderQuotaC;

    private Integer createOrderQuotaD;

    private Integer createOrderQuotaE;

    private Integer createOrderStatus = 0;
}
