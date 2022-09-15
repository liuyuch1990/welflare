package com.unicorn.wsp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.unicorn.wsp.entity.WspDept;
import com.unicorn.wsp.entity.WspSwiper;
import com.unicorn.wsp.entity.WspSwiper;
import com.unicorn.wsp.entity.vo.WspSwiperVO;
import org.springframework.stereotype.Service;

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
public interface WspSwiperService extends IService<WspSwiper>  {

    WspSwiper getInfoById(String id);

    boolean save(WspSwiperVO dto);

    boolean updateById(WspSwiperVO dto);

    boolean removeById(String id);

    IPage<WspSwiper> page(WspSwiperVO dto);

}

