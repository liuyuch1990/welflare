package com.unicorn.wsp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.unicorn.wsp.entity.WspIndexPage;
import com.unicorn.wsp.entity.vo.WspIndexPageVO;
import com.unicorn.wsp.mapper.WspIndexPageMapper;
import com.unicorn.wsp.common.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
/**
* <p>
    *  服务接口
    * </p>
*
* @author spark
* @since 1.0
*/
@Service
public interface WspIndexPageService extends IService<WspIndexPage>  {

    /**
     * 迭代：公司唯一
     * */
    boolean isExit(String com);

    /**
     * 迭代：查询
     * */
    WspIndexPage queryIndexPageInfoByCom(String comNum);

    /**
     * 迭代：编辑
     * */
    boolean editIndexPageInfo(WspIndexPage vo);



    WspIndexPage getInfoById(Long id);

    boolean save(WspIndexPageVO dto);

    boolean updateById(WspIndexPageVO dto);

    boolean removeById(Long id);

    IPage<WspIndexPage> page(WspIndexPageVO dto);


}

