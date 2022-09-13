package com.unicorn.wsp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.unicorn.wsp.entity.WspGiftCard;
import com.unicorn.wsp.entity.WspUser;
import com.unicorn.wsp.entity.vo.WspGiftCardVO;
import com.unicorn.wsp.entity.zznvo.GiftCardPageVo;
import com.unicorn.wsp.entity.zznvo.GiftCardVo;
import com.unicorn.wsp.mapper.WspGiftCardMapper;
import com.unicorn.wsp.common.result.Result;
import com.unicorn.wsp.vo.LoginVo;
import com.unicorn.wsp.vo.PageVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
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
public interface WspGiftCardService extends IService<WspGiftCard>  {

    WspGiftCard getInfoById(String id);

    boolean save(WspGiftCardVO dto);

    boolean updateById(WspGiftCardVO dto);

    boolean removeById(Long id);

    IPage<WspGiftCard> page(WspGiftCardVO dto);

    // 用户兑换卡号
    int exchange(String cardNum,String userId,String phone);

    // 校验卡号是否存在
    boolean isExist(String cardNum);


    /**
     * 清除礼品卡绑定状态
     * */
    boolean unbound(int giftCardId);

    /**
     * 绑定用
     * 迭代：绑定时判断是否存在时，要根据公司编号查询
     * */
    boolean isExist(String cardNum, String comNum);

    // 查询礼品卡所有信息
    PageVo<GiftCardVo> queryCardInfoList(GiftCardPageVo vo);

    // spark根据用户id查 有效的，未使用的 礼品卡全部信息（用户角度）
    HashMap<String, Object> queryCardByUserId(String id);

    // spark 下过订单之后把isUse置2
    int isUse(Integer cardId);

    // 临时 不让用户添加多张卡片
    Integer onlyOneCard(String wspUser);

    Integer onlyOneCard(LoginVo loginVo, String userId);

    // 取消订单后重置(礼品卡卡号，商品id，商品销量)
    boolean reSetCard(String cardNum);

    // 校验卡号是否存在登录判断
    boolean isExistLogin(String cardNum);

    /**
     * 登录用
     * 迭代：根据公司校验是礼品卡是否存在
     * */
    boolean isExistLogin(String cardNum, String comNum);

    /**
     * 迭代
     * 每天4点半判断是否到期
     * */
    void overdueHandler();


    /**
     * 迭代修改：判断是否过期, wsp_gift_card表更新isTrue字段, 返回isTrue（参数 date为该卡的有效期）
     */
    String changeValid(String date);


}

