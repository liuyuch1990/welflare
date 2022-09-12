package com.unicorn.wsp.common.base;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class BaseTreeNode implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 父菜单名称 */
    private String parentName;

    /** 父菜单ID */
    private Long parentId;

    /** 祖级列表 */
    private String ancestors;

    /** 子部门 */
    private List<Object> children = new ArrayList<>();
}
