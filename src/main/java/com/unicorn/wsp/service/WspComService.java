package com.unicorn.wsp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.unicorn.wsp.entity.SimpleDict;
import com.unicorn.wsp.entity.WspCom;
import com.unicorn.wsp.entity.vo.WspComVO;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
* <p>
    *  服务接口
    * </p>
*
* @author spark
* @since 1.0
*/
@Service
public interface WspComService extends IService<WspCom>  {

    WspCom getInfoById(Long id);

    boolean save(WspComVO dto);

    boolean updateById(WspComVO dto);

    boolean removeById(Long id);

    IPage<WspCom> page(WspComVO dto);

    // 查是否存在公司编号
    int checkCom(String conNum);

    // 公司编号下拉
    List<String> queryComNumDropDownList(HttpServletRequest request);

    /**
     * 公司list
     * @return
     */
    List<WspCom> queryComNumList();

    /**
     * 迭代：根据公司得到类型数据List
     * */
    List<String> queryTypeListByCom(String comNum);

    /**
     *  迭代 查公司对应商品类型Map
     * */
    List<Map<String, String>> queryTypeMapByCom(String comNum);

    /**
     * 迭代：添加新的商品种类
     * */
    int addGoodsType(SimpleDict dict, String comNum);

    /**
     * 迭代：添加公司的商品类型
     * */
    int addGoodsTypeByCom(String goodsType, String comNum);

    /**
     * 迭代：删除公司的商品类型
     * */
    boolean deleteGoodsType(String goodsType, String comNum);

    /**
     * 删除公司
     * */
    boolean dropCom(String comNum);



}

