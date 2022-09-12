package com.unicorn.wsp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.unicorn.wsp.entity.WspGoods;
import com.unicorn.wsp.entity.WspGoodsPic;
import com.unicorn.wsp.entity.vo.WspGoodsVO;
import com.unicorn.wsp.entity.zznvo.GoodsVo;
import com.unicorn.wsp.entity.zznvo.PicVo;
import com.unicorn.wsp.mapper.WspGoodsMapper;
import com.unicorn.wsp.common.result.Result;
import com.unicorn.wsp.vo.PageVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Service
public interface WspGoodsService extends IService<WspGoods>  {

    WspGoods getInfoById(Long id);

    boolean save(WspGoodsVO dto);

    boolean updateById(WspGoodsVO dto);

    boolean removeById(Long id);

    PageVo<PicVo> page1(GoodsVo dto);

    PageVo<PicVo> page(WspGoodsVO dto);

    List<WspGoods> queryById(String id);

    boolean inventoryReduction(String goodsId,Integer num);

    void delGoods(String goodsId);

    void clearPic();

    void clearLocalPic(List<WspGoodsPic> pics);

    /**
     * 编辑商品
     * */
    boolean editGoods(WspGoods goods);


    /**
     * 迭代：删除图片对应本地文件
     */
    void delLocalPic(WspGoodsPic pic);



}

