package com.unicorn.wsp.service.impl;



import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.unicorn.wsp.common.annotation.Excel;
import com.unicorn.wsp.common.exception.BusinessRollbackException;
import com.unicorn.wsp.common.result.Result;
import com.unicorn.wsp.entity.SimpleDict;
import com.unicorn.wsp.entity.WspGoods;
import com.unicorn.wsp.entity.WspGoodsPic;
import com.unicorn.wsp.entity.exportvo.WspGoodsETO;
import com.unicorn.wsp.entity.vo.WspGoodsVO;
import com.unicorn.wsp.entity.zznvo.GoodsVo;
import com.unicorn.wsp.entity.zznvo.PicVo;
import com.unicorn.wsp.mapper.SimpleDictMapper;
import com.unicorn.wsp.mapper.WspGoodsMapper;
import com.unicorn.wsp.mapper.WspGoodsPicMapper;
import com.unicorn.wsp.service.SimpleDictService;
import com.unicorn.wsp.service.WspGoodsPicService;
import com.unicorn.wsp.service.WspGoodsService;
import com.unicorn.wsp.utils.DozerUtil;
import com.unicorn.wsp.vo.PageVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@Slf4j
public class WspGoodsServiceImpl extends ServiceImpl<WspGoodsMapper,WspGoods>
    implements WspGoodsService {

    @Autowired
    WspGoodsMapper wspGoodsMapper;

    @Autowired
    WspGoodsPicMapper wspGoodsPicMapper;

    @Autowired
    WspGoodsPicService picService;

    @Autowired
    SimpleDictMapper dictMapper;

    //文件上传目录
    @Value("${upload.dir}")
    private String uploadDirectory;

    // 图片访问路径前缀
    @Value("${upload.path}")
    private String path;



    // 根據id查詢個體
    @Override
    public List<WspGoods> queryById(String id){
        LambdaQueryWrapper<WspGoods> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WspGoods::getGoodsId, id);
        wrapper.eq(WspGoods::getIsDel,0);
        List<WspGoods> wspGoods = wspGoodsMapper.selectList(wrapper);
        return wspGoods;
    }

    /**
     * 迭代修改：根据公司的typeCodeList查商品
     * */
    @Override
    public PageVo<PicVo> page1(GoodsVo dto) {
        LambdaQueryWrapper<WspGoods> queryWrapper = new LambdaQueryWrapper<>();
        log.info("商品 条件查询 dto - {}",dto);

        // 指定了商品种类
        if (ObjectUtil.isNotNull(dto.getGoodsType())) {
            // 商品种类能匹配上
            if(dto.getTypeList().contains(dto.getGoodsType()))
                // 售罄的
                if(ObjectUtil.isNotNull(dto.getSellOut()) && "1".equals(dto.getSellOut())){
                    queryWrapper.eq(WspGoods::getGoodsType,dto.getGoodsType())
                            .eq(WspGoods::getGoodsSum, 0);
                }
                // 未售罄的
                else if(ObjectUtil.isNotNull(dto.getSellOut()) && "0".equals(dto.getSellOut())){
                    queryWrapper.eq(WspGoods::getGoodsType,dto.getGoodsType())
                            .ne(WspGoods::getGoodsSum, 0);
                }
                // 全部的
                else{
                    queryWrapper.eq(WspGoods::getGoodsType,dto.getGoodsType());
                }
            // 指定了不匹配的商品种类
            else
                // 如果输入type在对应公司中不存在，则查询一个不存在的goodsType（goodsType数据库字段长度：3）
                queryWrapper.eq(WspGoods::getGoodsType, "404-404-404");
        // 未指定商品种类
        }else{
            // 售罄的
            if(ObjectUtil.isNotNull(dto.getSellOut()) && "1".equals(dto.getSellOut())){
                dto.getTypeList().forEach(type -> queryWrapper
                        .or().eq(WspGoods::getGoodsType, type)
                        .eq(WspGoods::getGoodsSum, 0));
            }
            // 未售罄的
            else if(ObjectUtil.isNotNull(dto.getSellOut()) && "0".equals(dto.getSellOut())){
                dto.getTypeList().forEach(type -> queryWrapper
                        .or().eq(WspGoods::getGoodsType, type)
                        .ne(WspGoods::getGoodsSum, 0));
            }
            // 全部的
            else{
                dto.getTypeList().forEach(type -> queryWrapper.or().eq(WspGoods::getGoodsType, type));
            }
        }

        // 按照创建时间排序
        queryWrapper.orderByAsc(WspGoods::getCreatedDate);
        PageHelper.startPage(dto.getPageNum(), dto.getPageSize());
        List<WspGoods> wspGoods = wspGoodsMapper.selectList(queryWrapper);

        // list 轉換
        List<PicVo> picVos = DozerUtil.mapList(wspGoods, PicVo.class);
        System.out.println("picVos = " + picVos);

        // 新业务：再字典表里查出字段商品类型名
        LambdaQueryWrapper<SimpleDict> dictLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dictLambdaQueryWrapper.eq(SimpleDict::getDictType,"goodsType");
        List<SimpleDict> dicts = dictMapper.selectList(dictLambdaQueryWrapper);

        log.info("新业务 - {}",dicts);
        for(PicVo r : picVos){

            // 新业务：再字典表里查出字段商品类型名并赋值
            for(SimpleDict dict: dicts){
                if(r.getGoodsType().equals(dict.getDictCode())){
                    r.setTypeName(dict.getDictName());
                }
            }

            // 查圖片賦給picvo
            String s = picService.queryCoverByGoodId(r.getGoodsId());
            if(!"nothing".equals(s)){
                r.setCoverPath(s);
            }

            // 不需要把所有圖片都查出來
            /*List<WspGoodsPic> wspGoodsPics = picService.queryListByGoodId(r.getGoodsId());
            for(WspGoodsPic pic: wspGoodsPics){
                if("1".equals(pic.getIsCover())){
                    r.setCoverPath(pic.getPicSavepath());
                    break;
                }
            }
            r.setPics(wspGoodsPics); */
        }

        // 为了拿到正确的pageTotal，临时代替pageInfo.getTotal
        PageInfo<WspGoods> tempPageInfo = new PageInfo<>(wspGoods);


        PageInfo<PicVo> pageInfo = new PageInfo<>(picVos);
        PageVo<PicVo> pageVo = new PageVo<>(pageInfo.getPageNum(), pageInfo.getSize(), picVos, tempPageInfo.getTotal());


        return pageVo;
    }

    /*
     * 下过订单库存减X，加销量
     * goodId 商品id
     * num  减少的数量
     * */
    @Override
    public boolean inventoryReduction(String goodsId, Integer num){
        WspGoods goods = wspGoodsMapper.selectById(goodsId);
        if(ObjectUtil.isNull(goods)){
            return false;
        }
        // 如果 商品数量大于等于下单数
        if(goods.getGoodsSum()>=num){
            WspGoods editGoods = new WspGoods();
            editGoods.setGoodsId(goodsId);
            Integer editGoodSum = goods.getGoodsSum() - num;
            editGoods.setGoodsSum(editGoodSum);
            int nowSales = goods.getGoodsSales() + num;
            editGoods.setGoodsSales(nowSales);
            wspGoodsMapper.updateById(editGoods);
            return true;
        }else{
            return false;
        }
    }


    /**
     * 编辑商品
     * */
    public boolean editGoods(WspGoods goods){
        int i = wspGoodsMapper.updateById(goods);
        if(i>1) throw new BusinessRollbackException("商品数据异常");
        return i==1;
    }

    // 删除商品
    @Override
    @Transactional
    public void delGoods(String goodsId){

        LambdaQueryWrapper<WspGoods> goodsWrapper = new LambdaQueryWrapper<>();
        goodsWrapper.eq(WspGoods::getGoodsId, goodsId);

        LambdaQueryWrapper<WspGoodsPic> picWrapper = new LambdaQueryWrapper<>();
        picWrapper.eq(WspGoodsPic::getGoodsId, goodsId);
        List<WspGoodsPic> pics = wspGoodsPicMapper.selectList(picWrapper);
        for(WspGoodsPic pic : pics){
            // 1先删本地图片
            delLocalPic(pic);
            // 2再删图片表
            wspGoodsPicMapper.deleteById(pic.getPicId());
        }
        // 3再删商品表
        wspGoodsMapper.deleteById(goodsId);
    }

    // 清空和商品表无关的'文件'和'图片表数据'
    @Override
    @Transactional
    public void clearPic(){
        LambdaQueryWrapper<WspGoods> goodsWrapper = new LambdaQueryWrapper<>();
        List<WspGoods> goodsList = wspGoodsMapper.selectList(goodsWrapper);
        LambdaQueryWrapper<WspGoodsPic> picWrapper = new LambdaQueryWrapper<>();
        List<WspGoodsPic> pics = wspGoodsPicMapper.selectList(picWrapper);

        List<WspGoodsPic> pendingPicList = new ArrayList<>();
        for(WspGoodsPic pic : pics){
            // 如果对应商品为空,加入待删list
            if(StringUtils.isEmpty(pic.getGoodsId())){
                pendingPicList.add(pic);
                continue;
            }
            // 如果goodsIs商品表中不存在,加入待删list
            int count = 0;
            for(WspGoods goods: goodsList){
                if(!pic.getGoodsId().equals(goods.getGoodsId())){
                    count += 1;
                }
            }
            if(count == goodsList.size()){
                pendingPicList.add(pic);
            }
        }

        for(WspGoodsPic pic : pendingPicList){
            // 1先删本地图片
            delLocalPic(pic);
            // 2再删图片表
            wspGoodsPicMapper.deleteById(pic.getPicId());
        }
        // 清空和图片表无关的本地文件
        clearLocalPic(pics);
    }

    // 清空和图片表无关本地文件
    @Override
    public void clearLocalPic(List<WspGoodsPic> pics){
        File dir = new File(uploadDirectory);
        File[] files = dir.listFiles();
        List<File> pendingFiles = new ArrayList<>();
        for(File file : files){
            String fileName = file.getName();
            int count = 0;
            for(WspGoodsPic pic : pics){
                String picName = pic.getPicSavepath().substring(6);
                if(picName.equals(fileName)){
                    count += 1;
                }
            }
            if(count == 0) {
                pendingFiles.add(file);
            }
        }
        for(File file: pendingFiles){
            file.delete();
        }
    }

    // 删除图片对应本地文件
    public void delLocalPic(WspGoodsPic pic){
        String path = uploadDirectory + pic.getPicSavepath().substring(6);
        log.info("删除商品图片地址:{}", path);
        File file = new File(path);
        boolean exists = file.exists();
        if(exists){
            boolean delete = file.delete();
            log.info("图片删除：{}", delete);
        }else{
            log.info("---1---{}图片不存在", pic.getPicSavepath());
            log.info("---2---商品:{}", pic.getGoodsId());
        }
    }



////////////////////////////////////////////////////////////////////


    @Override
    public PageVo<PicVo> page(WspGoodsVO dto) {
        return null;
    }


    @Override
    public WspGoods getInfoById(Long id) {
        return super.getById(id);
    }

    @Override
    public boolean save(WspGoodsVO dto) {
        WspGoods entity = new WspGoods();
        BeanUtils.copyProperties(dto, entity);
        return super.save(entity);
    }

    @Override
    public boolean updateById(WspGoodsVO dto) {
        WspGoods entity = new WspGoods();
        BeanUtils.copyProperties(dto, entity);
        return super.updateById(entity);
    }

    @Override
    public boolean removeById(Long id) {
        return super.removeById(id);
    }


}
