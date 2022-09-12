package com.unicorn.wsp.common.base;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * base entity 公共字段
 * @author zy
 */
@Data
public class BaseEntity implements Serializable {

//    private static final long serialVersionUID = 1L;
//    @TableId(value = "id", type = IdType.AUTO)
//    private Integer id;
//    /** 创建者 */
//    @TableField(value = "create_by",fill = FieldFill.INSERT)
//    private String createBy;
//    /** 创建时间 */
//    @TableField(value = "create_time",fill = FieldFill.INSERT)
//    private Date createTime;
//    /** 修改者 */
//    @TableField(value = "modify_by",fill = FieldFill.INSERT_UPDATE)
//    private String modifyBy;
//    /** 修改时间 */
//    @TableField(value = "modify_time",fill = FieldFill.INSERT_UPDATE)
//    private Date modifyTime;
//
//    @TableField(exist = false)
//    private String createName;

}
