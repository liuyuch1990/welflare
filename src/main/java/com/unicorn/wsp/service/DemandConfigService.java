package com.unicorn.wsp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.unicorn.wsp.entity.DemandConfig;
import com.unicorn.wsp.entity.vo.DemandConfigVO;
import com.unicorn.wsp.mapper.DemandConfigMapper;
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
public interface DemandConfigService extends IService<DemandConfig>  {

    DemandConfig getInfoById(Long id);

    boolean save(DemandConfigVO dto);

    boolean updateById(DemandConfigVO dto);

    boolean removeById(Long id);

    IPage<DemandConfig> page(DemandConfigVO dto);

    // 判断需求是否启用
    boolean checkDemand(int id, int configType);

    // 判断需求是否启用2
    int checkDemand(int id);

}

