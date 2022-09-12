package com.unicorn.wsp.entity.exportvo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
* 
*
* @Author spark
* @Version 1.0
*/
@Data
@EqualsAndHashCode(callSuper = false)
public class WspRoleMenuETO {

    /**
    * id
    */
    private Integer id;

    /**
    * 菜单id
    */
    private Integer menuId;

    /**
    * 角色id
    */
    private Integer roleId;



}