package com.unicorn.wsp.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.unicorn.wsp.entity.WspUserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
* <p>
*  spark
* </p>
*
* @author spark
* @since 1.0
*/
@Mapper
public interface WspUserRoleMapper extends BaseMapper<WspUserRole>{

    @Select("select role_id " +
            "from wsp_user_role b  " +
            "where b.user_id = #{userId}  " +
            "limit 1")
    Integer queryAdminLevel(@Param("userId") String userId);


}

