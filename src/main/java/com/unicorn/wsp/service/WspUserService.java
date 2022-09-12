package com.unicorn.wsp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.unicorn.wsp.entity.WspUser;
import com.unicorn.wsp.entity.vo.WspUserVO;
import com.unicorn.wsp.entity.zznvo.UserPageVo;
import com.unicorn.wsp.mapper.WspUserMapper;
import com.unicorn.wsp.common.result.Result;
import com.unicorn.wsp.vo.PageVo;
import com.unicorn.wsp.vo.UserVo;
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
public interface WspUserService extends IService<WspUser>  {

    WspUser getInfoById(Long id);

    boolean save(WspUserVO dto);

    boolean updateById(WspUserVO dto);

    boolean removeById(Long id);

    IPage<WspUser> page(WspUserVO dto);

    List<WspUser> getUser(String phone);

    // 用户 根据id
    WspUser getUserById(String id);

    // 用户 list
    PageVo<WspUser> queryList(UserPageVo user);

    // 根据手机号查用户
    WspUser queryUserByPhone(String phone);

    /**
     * 用户list，增加了权限字段
     * */
    PageVo<UserVo> queryListWithRoleId(UserPageVo user);

    /**
     * 编辑用户权限
     * */
    boolean editUserCom(WspUser user);




}

