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

    //??????????????????
    @Value("${upload.dir}")
    private String uploadDirectory;

    // ????????????????????????
    @Value("${upload.path}")
    private String path;



    // ??????id????????????
    @Override
    public List<WspGoods> queryById(String id){
        LambdaQueryWrapper<WspGoods> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WspGoods::getGoodsId, id);
        wrapper.eq(WspGoods::getIsDel,0);
        List<WspGoods> wspGoods = wspGoodsMapper.selectList(wrapper);
        return wspGoods;
    }

    /**
     * ??????????????????????????????typeCodeList?????????
     * */
    @Override
    public PageVo<PicVo> page1(GoodsVo dto) {
        LambdaQueryWrapper<WspGoods> queryWrapper = new LambdaQueryWrapper<>();
        log.info("?????? ???????????? dto - {}",dto);

        // ?????????????????????
        if (ObjectUtil.isNotNull(dto.getGoodsType())) {
            // ????????????????????????
            if(dto.getTypeList().contains(dto.getGoodsType()))
                // ?????????
                if(ObjectUtil.isNotNull(dto.getSellOut()) && "1".equals(dto.getSellOut())){
                    queryWrapper.eq(WspGoods::getGoodsType,dto.getGoodsType())
                            .eq(WspGoods::getGoodsSum, 0);
                }
                // ????????????
                else if(ObjectUtil.isNotNull(dto.getSellOut()) && "0".equals(dto.getSellOut())){
                    queryWrapper.eq(WspGoods::getGoodsType,dto.getGoodsType())
                            .ne(WspGoods::getGoodsSum, 0);
                }
                // ?????????
                else{
                    queryWrapper.eq(WspGoods::getGoodsType,dto.getGoodsType());
                }
            // ?????????????????????????????????
            else
                // ????????????type?????????????????????????????????????????????????????????goodsType???goodsType????????????????????????3???
                queryWrapper.eq(WspGoods::getGoodsType, "404-404-404");
        // ?????????????????????
        }else{
            // ?????????
            if(ObjectUtil.isNotNull(dto.getSellOut()) && "1".equals(dto.getSellOut())){
                dto.getTypeList().forEach(type -> queryWrapper
                        .or().eq(WspGoods::getGoodsType, type)
                        .eq(WspGoods::getGoodsSum, 0));
            }
            // ????????????
            else if(ObjectUtil.isNotNull(dto.getSellOut()) && "0".equals(dto.getSellOut())){
                dto.getTypeList().forEach(type -> queryWrapper
                        .or().eq(WspGoods::getGoodsType, type)
                        .ne(WspGoods::getGoodsSum, 0));
            }
            // ?????????
            else{
                dto.getTypeList().forEach(type -> queryWrapper.or().eq(WspGoods::getGoodsType, type));
            }
        }

        // ????????????????????????
        queryWrapper.orderByAsc(WspGoods::getCreatedDate);
        PageHelper.startPage(dto.getPageNum(), dto.getPageSize());
        List<WspGoods> wspGoods = wspGoodsMapper.selectList(queryWrapper);

        // list ??????
        List<PicVo> picVos = DozerUtil.mapList(wspGoods, PicVo.class);
        System.out.println("picVos = " + picVos);

        // ??????????????????????????????????????????????????????
        LambdaQueryWrapper<SimpleDict> dictLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dictLambdaQueryWrapper.eq(SimpleDict::getDictType,"goodsType");
        List<SimpleDict> dicts = dictMapper.selectList(dictLambdaQueryWrapper);

        log.info("????????? - {}",dicts);
        for(PicVo r : picVos){

            // ???????????????????????????????????????????????????????????????
            for(SimpleDict dict: dicts){
                if(r.getGoodsType().equals(dict.getDictCode())){
                    r.setTypeName(dict.getDictName());
                }
            }

            // ???????????????picvo
            String s = picService.queryCoverByGoodId(r.getGoodsId());
            if(!"nothing".equals(s)){
                r.setCoverPath(s);
            }

            // ????????????????????????????????????
            /*List<WspGoodsPic> wspGoodsPics = picService.queryListByGoodId(r.getGoodsId());
            for(WspGoodsPic pic: wspGoodsPics){
                if("1".equals(pic.getIsCover())){
                    r.setCoverPath(pic.getPicSavepath());
                    break;
                }
            }
            r.setPics(wspGoodsPics); */
        }

        // ?????????????????????pageTotal???????????????pageInfo.getTotal
        PageInfo<WspGoods> tempPageInfo = new PageInfo<>(wspGoods);


        PageInfo<PicVo> pageInfo = new PageInfo<>(picVos);
        PageVo<PicVo> pageVo = new PageVo<>(pageInfo.getPageNum(), pageInfo.getSize(), picVos, tempPageInfo.getTotal());


        return pageVo;
    }

    /*
     * ?????????????????????X????????????
     * goodId ??????id
     * num  ???????????????
     * */
    @Override
    public boolean inventoryReduction(String goodsId, Integer num){
        WspGoods goods = wspGoodsMapper.selectById(goodsId);
        if(ObjectUtil.isNull(goods)){
            return false;
        }
        // ?????? ?????????????????????????????????
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
     * ????????????
     * */
    public boolean editGoods(WspGoods goods){
        int i = wspGoodsMapper.updateById(goods);
        if(i>1) throw new BusinessRollbackException("??????????????????");
        return i==1;
    }

    // ????????????
    @Override
    @Transactional
    public void delGoods(String goodsId){

        LambdaQueryWrapper<WspGoods> goodsWrapper = new LambdaQueryWrapper<>();
        goodsWrapper.eq(WspGoods::getGoodsId, goodsId);

        LambdaQueryWrapper<WspGoodsPic> picWrapper = new LambdaQueryWrapper<>();
        picWrapper.eq(WspGoodsPic::getGoodsId, goodsId);
        List<WspGoodsPic> pics = wspGoodsPicMapper.selectList(picWrapper);
        for(WspGoodsPic pic : pics){
            // 1??????????????????
            delLocalPic(pic);
            // 2???????????????
            wspGoodsPicMapper.deleteById(pic.getPicId());
        }
        // 3???????????????
        wspGoodsMapper.deleteById(goodsId);
    }

    // ???????????????????????????'??????'???'???????????????'
    @Override
    @Transactional
    public void clearPic(){
        LambdaQueryWrapper<WspGoods> goodsWrapper = new LambdaQueryWrapper<>();
        List<WspGoods> goodsList = wspGoodsMapper.selectList(goodsWrapper);
        LambdaQueryWrapper<WspGoodsPic> picWrapper = new LambdaQueryWrapper<>();
        List<WspGoodsPic> pics = wspGoodsPicMapper.selectList(picWrapper);

        List<WspGoodsPic> pendingPicList = new ArrayList<>();
        for(WspGoodsPic pic : pics){
            // ????????????????????????,????????????list
            if(StringUtils.isEmpty(pic.getGoodsId())){
                pendingPicList.add(pic);
                continue;
            }
            // ??????goodsIs?????????????????????,????????????list
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
            // 1??????????????????
            delLocalPic(pic);
            // 2???????????????
            wspGoodsPicMapper.deleteById(pic.getPicId());
        }
        // ???????????????????????????????????????
        clearLocalPic(pics);
    }

    // ????????????????????????????????????
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

    // ??????????????????????????????
    public void delLocalPic(WspGoodsPic pic){
        String path = uploadDirectory + pic.getPicSavepath().substring(6);
        log.info("????????????????????????:{}", path);
        File file = new File(path);
        boolean exists = file.exists();
        if(exists){
            boolean delete = file.delete();
            log.info("???????????????{}", delete);
        }else{
            log.info("---1---{}???????????????", pic.getPicSavepath());
            log.info("---2---??????:{}", pic.getGoodsId());
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
