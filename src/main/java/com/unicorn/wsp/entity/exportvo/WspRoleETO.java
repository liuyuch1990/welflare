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
public class WspRoleETO {

    /**
    * id
    */
    private Integer roleId;

    /**
    * 角色名
    */
    private String roleName;



}