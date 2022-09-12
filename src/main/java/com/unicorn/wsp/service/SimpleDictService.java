package com.unicorn.wsp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.unicorn.wsp.entity.SimpleDict;
import com.unicorn.wsp.entity.vo.SimpleDictVO;
import com.unicorn.wsp.mapper.SimpleDictMapper;
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
public interface SimpleDictService extends IService<SimpleDict>  {


    /**
     * 迭代：根据type查list
     */
    List<SimpleDict> queryDict(String dictType);











    SimpleDict getInfoById(Long id);

    boolean save(SimpleDictVO dto);

    boolean updateById(SimpleDictVO dto);

    boolean removeById(Long id);

    IPage<SimpleDict> page(SimpleDictVO dto);



}

