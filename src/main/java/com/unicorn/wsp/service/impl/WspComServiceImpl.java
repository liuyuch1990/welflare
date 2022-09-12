package com.unicorn.wsp.service.impl;



import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.unicorn.wsp.common.exception.BusinessRollbackException;
import com.unicorn.wsp.entity.SimpleDict;
import com.unicorn.wsp.entity.WspCom;
import com.unicorn.wsp.entity.vo.WspComVO;
import com.unicorn.wsp.mapper.WspComMapper;
import com.unicorn.wsp.service.SimpleDictService;
import com.unicorn.wsp.service.WspComService;
import com.unicorn.wsp.utils.TokenUtils;
import com.unicorn.wsp.vo.AccessToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
* <p>
    *  服务类
    * </p>
*
* @author spark
* @since 1.0
*/
@Service
public class WspComServiceImpl extends ServiceImpl<WspComMapper,WspCom>
    implements WspComService {

    @Autowired
    WspComMapper comMapper;

    @Autowired
    SimpleDictService dictService;

    /**
     * 迭代：删除公司的商品类型
     * */
    public boolean deleteGoodsType(String goodsType, String comNum){
        List<String> list = queryTypeListByCom(comNum);
        HashSet<String> editSet = new HashSet<>(list);
        System.out.println(list);
        if(ObjectUtil.isEmpty(editSet)) return false;
        editSet.remove(goodsType);
        int i = editGoodsTypeCode(editSet, comNum);
        return i==1;
    }

    /**
     * 删除公司
     * */
    public boolean dropCom(String comNum) {
        LambdaQueryWrapper<WspCom> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WspCom::getComNum, comNum);
        int delete = comMapper.delete(wrapper);
        return delete>0;
    }


    /**
     * 迭代：添加新的商品种类
     * */
    @Transactional
    public int addGoodsType(SimpleDict dict, String comNum){
        List<SimpleDict> goodsTypeList = dictService.queryDict("goodsType");
        if(ObjectUtil.isNotEmpty(goodsTypeList)){
            for (SimpleDict type: goodsTypeList) {
                if(dict.getDictCode().equals(type.getDictCode())) return 2;
            }
        }

        SimpleDict insertDict = new SimpleDict();
        insertDict.setDictType("goodsType");
        insertDict.setDictCode(dict.getDictCode());
        insertDict.setDictName(dict.getDictName());
        boolean save = dictService.save(insertDict);
        if(!save) return 3;
        int i = addGoodsTypeByCom(dict.getDictCode(), comNum);
        return i;
    }



    /**
     * 迭代：添加公司的商品类型
     * */
    public int addGoodsTypeByCom(String goodsType, String comNum){
        // 校验商品是否存在SIMPLE_DICT表
        List<SimpleDict> goodsTypeList = dictService.queryDict("goodsType");
        ArrayList<String> list = new ArrayList<>();
        goodsTypeList.forEach(type->{
            list.add(type.getDictCode());
        });
        boolean contains = list.contains(goodsType);
        if(!contains) return 2;

        // 组装新type
        List<String> editList = queryTypeListByCom(comNum);
        editList.add(goodsType);
        HashSet<String> set = new HashSet<>(editList);
        int i = editGoodsTypeCode(set, comNum);
        return i;
    }

    /**
     * 迭代：编辑公司TypeCodeList
     * */
    public int editGoodsTypeCode(HashSet set, String comNum){
        StringBuffer buffer = new StringBuffer();
        set.forEach(str->{
            buffer.append(str+",");
        });

        String editTypes;
        if(buffer==null||buffer.toString().equals("")){
            editTypes = "";
        }else {
            editTypes = buffer.substring(0, buffer.length() - 1);
        }
        // update
        LambdaQueryWrapper<WspCom> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WspCom::getComNum, comNum);
        WspCom com = new WspCom();
        com.setGoodsTypeCode(editTypes);
        boolean update = super.update(com, wrapper);
        return update? 1:0;
    }



    /**
     * 迭代 查公司对应商品类型Map
     * */
    public List<Map<String, String>> queryTypeMapByCom(String comNum){
        // 1先查公司对应商品类型code
        LambdaQueryWrapper<WspCom> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WspCom::getComNum, comNum);
        WspCom wspCom = null;
        try{
            wspCom = comMapper.selectOne(wrapper);
        }catch (Exception e){
            throw new BusinessRollbackException("公司数据不唯一");
        }
        ArrayList<Map<String, String>> maps = new ArrayList<>();
        if(ObjectUtil.isEmpty(wspCom)) return maps;

        // 2再去字典表查商品类型数据（goodsTypeCode,goodsTypeName）
        List<String> list = restructureGoodsType(wspCom.getGoodsTypeCode());
        List<SimpleDict> goodsTypeList = dictService.queryDict("goodsType");
        HashMap<String, String> goodsTypeMap = new HashMap<>();

        // 3封装数据
        goodsTypeList.forEach(type->{
            goodsTypeMap.put(type.getDictCode(), type.getDictName());
        });
        list.forEach(code->{
            HashMap<String, String> map = new HashMap<>();
            map.put("dictCode", code);
            map.put("dictName", goodsTypeMap.get(code));
            maps.add(map);
        });
        return maps;
    }


    /**
     * 迭代 查公司对应商品类型下拉List
     * */
    public List<String> queryTypeListByCom(String comNum){
        LambdaQueryWrapper<WspCom> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WspCom::getComNum, comNum);
        WspCom wspCom = null;
        try{
            wspCom = comMapper.selectOne(wrapper);
        }catch (Exception e){
            throw new BusinessRollbackException("公司数据不唯一");
        }

        if(ObjectUtil.isEmpty(wspCom)) return new ArrayList<>();
        return restructureGoodsType(wspCom.getGoodsTypeCode());
    }


    /** 迭代 商品类型String重构成 List<String>*/
    List<String> restructureGoodsType(String strs){
        List<String> list;
        
        if(strs==null||strs.equals("")) {
            list = Collections.emptyList();
        }else{
            String[] str = strs.split(",");
            list = new ArrayList<>(Arrays.asList(str));
        }
        return list;
    }


    // 公司编号下拉
    public List<String> queryComNumDropDownList(HttpServletRequest request){
        AccessToken accessToken = TokenUtils.analyseRequest(request);
        LambdaQueryWrapper<WspCom> wspComLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //判断roleId是否大于1，若大于1，则为管理员用户
        int roleId = accessToken.getRoleId();
        if(roleId > 1){
            wspComLambdaQueryWrapper.eq(WspCom::getComNum,accessToken.getComNum());
        }
        List<WspCom> wspComs = comMapper.selectList(wspComLambdaQueryWrapper);
        ArrayList<String> list = new ArrayList<>();
        for(WspCom com : wspComs){
            list.add(com.getComNum());
        }
        return list;
    }

    /**
     * 公司list
     * @return
     */
    public List<WspCom> queryComNumList() {
        LambdaQueryWrapper<WspCom> wspComLambdaQueryWrapper = new LambdaQueryWrapper<>();
        List<WspCom> wspComs = comMapper.selectList(wspComLambdaQueryWrapper);
        return wspComs;
    }

    public int checkCom(String conNum){
        LambdaQueryWrapper<WspCom> comWrapper = new LambdaQueryWrapper<>();
        comWrapper.eq(WspCom::getComNum, conNum);
        Integer count = comMapper.selectCount(comWrapper);
        return count;
    }






















    /////////////////////////////////////////////////////////////////////

    @Override
    public WspCom getInfoById(Long id) {
        return super.getById(id);
    }

    @Override
    public boolean save(WspComVO dto) {
        WspCom entity = new WspCom();
        BeanUtils.copyProperties(dto, entity);
        return super.save(entity);
    }

    @Override
    public boolean updateById(WspComVO dto) {
        WspCom entity = new WspCom();
        BeanUtils.copyProperties(dto, entity);
        return super.updateById(entity);
    }

    @Override
    public boolean removeById(Long id) {
        return super.removeById(id);
    }

    @Override
    public IPage<WspCom> page(WspComVO dto) {
        LambdaQueryWrapper<WspCom> queryWrapper = new LambdaQueryWrapper<>();
        if (ObjectUtil.isNotNull(dto)) {

        }
        Page<WspCom> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        return this.page(page, queryWrapper);
    }
}
