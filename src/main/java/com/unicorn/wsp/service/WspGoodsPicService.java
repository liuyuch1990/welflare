package com.unicorn.wsp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.unicorn.wsp.entity.WspGoodsPic;
import com.unicorn.wsp.entity.vo.WspGoodsPicVO;
import com.unicorn.wsp.mapper.WspGoodsPicMapper;
import com.unicorn.wsp.common.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
public interface WspGoodsPicService extends IService<WspGoodsPic>  {

    WspGoodsPic getInfoById(Long id);

    boolean save(WspGoodsPicVO dto);

    boolean updateById(WspGoodsPicVO dto);

    boolean removeById(Long id);

    IPage<WspGoodsPic> page(WspGoodsPicVO dto);

    List<WspGoodsPic> queryListByGoodId(String id);

    String queryCoverByGoodId(String id);




}

