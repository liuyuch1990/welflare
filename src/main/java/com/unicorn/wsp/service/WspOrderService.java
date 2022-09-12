package com.unicorn.wsp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.unicorn.wsp.entity.WspOrder;
import com.unicorn.wsp.entity.vo.WspOrderVO;
import com.unicorn.wsp.mapper.WspOrderMapper;
import com.unicorn.wsp.common.result.Result;
import com.unicorn.wsp.utils.TokenUtils;
import com.unicorn.wsp.vo.EditLogisticsVo;
import com.unicorn.wsp.vo.OrderQueryVo;
import com.unicorn.wsp.vo.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
* <p>
    *  服务接口
    * </p>
*
* @author spark
* @since 1.0
*/
@Service
public interface WspOrderService extends IService<WspOrder>  {

    WspOrder getInfoById(Long id);

    boolean save(WspOrderVO dto);

    boolean updateById(WspOrderVO dto);

    boolean removeById(Long id);

    IPage<WspOrder> page(WspOrderVO dto);

    /**
     * 订单查询
     * @param wspOrderVO
     * @return
     */
    List<WspOrder> getOrderList(WspOrderVO wspOrderVO);

    /**
     * 批量导入物流
     * @param picture
     * @return
     * @throws IOException
     */
    Result batchShipment(MultipartFile picture) throws IOException;

    boolean updateByEntityId(WspOrder dto);


    /**
     * 订单查询,携带token
     * @param wspOrderVO
     * @return
     */
    List<OrderQueryVo> getOrderListToken(WspOrderVO wspOrderVO, HttpServletRequest request);


    /**
     * 管理员订单查询
     * @param wspOrderVO
     * @return
     */
    List<OrderQueryVo> getOrderPageList(WspOrderVO wspOrderVO);

    /**
     * 迭代：下单
     */
    Integer createOrder(WspOrderVO wspOrderVO, String userId);


    /**
     * 迭代: 是否消费完
     * */
    int quotaIsSpent(WspOrderVO wspOrderVO, String userId);


    // 根据用户id查订单 礼品卡校验用
    List<WspOrder> queryOrderByUserId(String userId);


    // 发货
    int sendOff(EditLogisticsVo dto);

    // 编辑物流
    int editLogistics(EditLogisticsVo dto);

    /**
     * 批量空发货
     * */
    boolean setBatchOrderStatusList(List<Integer> list);




}

