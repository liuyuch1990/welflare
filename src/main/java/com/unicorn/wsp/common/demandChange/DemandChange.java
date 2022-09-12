package com.unicorn.wsp.common.demandChange;

import cn.hutool.core.util.ObjectUtil;
import com.unicorn.wsp.entity.vo.WspOrderVO;
import com.unicorn.wsp.service.DemandConfigService;
import com.unicorn.wsp.service.WspGiftCardService;
import com.unicorn.wsp.service.WspOrderService;
import com.unicorn.wsp.vo.OrderGoodsVo;
import com.unicorn.wsp.vo.OrderQueryVo;
import com.unicorn.wsp.vo.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Component
public class DemandChange {

    @Autowired
    WspGiftCardService wspGiftCardService;

    @Autowired
    DemandConfigService demandConfigService;

    @Autowired
    WspOrderService wspOrderService;

    // 1,1) 检查礼品卡类型
    public boolean checkGiftType(WspOrderVO wspOrderVO){

        if (ObjectUtil.isNotEmpty(wspOrderVO.getOrderGoodsJson())) {
            //CreateOrderTestVo createOrderTestVo = new CreateOrderTestVo();
            List<OrderGoodsVo> orderGoodsJson = wspOrderVO.getOrderGoodsJson();
            for(OrderGoodsVo i : orderGoodsJson){
                for(OrderGoodsVo x : orderGoodsJson){
                    if(!i.getGoodsType().equals(x.getGoodsType())){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    // 2,1) 先查订单list, 再确认订单均已发货
    public boolean checkOrderState(WspOrderVO wspOrderVO, HttpServletRequest request){
        List<OrderQueryVo> orderList = wspOrderService.getOrderListToken(wspOrderVO, request);
        if(ObjectUtil.isNull(orderList) || orderList.size() == 0){
            // 如果没有未发货   --(就不能进行礼品卡数量判断,return ResultVO.fail)
            return true;
        }
        return false;
    }


    // 0) 验证需求是否启用
    public boolean queryDemand(int id, int configType){
        boolean b = demandConfigService.checkDemand(id, configType);
        return b;
    }

    // 0) 验证需求是否启用,切配置参数为
    public int queryDemand(int id){
        int b = demandConfigService.checkDemand(id);
        return b;
    }

}
