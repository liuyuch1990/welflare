package com.unicorn.wsp.common.base;

//import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * page dto
 * @author zy
 */
@Data
@ApiModel(value = "BasePageDto", description = "BasePageDto")
public class BasePageDto {

    @ApiModelProperty(value = "页码", name = "pageNum", example = "1")
    private Integer pageNum = 1;
    @ApiModelProperty(value = "每页大小", name = "pageSize", example = "10")
    private Integer pageSize = 20;
    @ApiModelProperty(value = "创建者", name = "createBy", example = "admin")
    private String createBy;
    @DateTimeFormat(pattern="yyyy-MM-dd")
//    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd",timezone = "GMT+8")
    @ApiModelProperty(value = "创建时间", name = "createTime", example = "2020-01-01")
    private Date createTime;
    @ApiModelProperty(value = "修改者", name = "modifyBy", example = "admin")
    private String modifyBy;
    @DateTimeFormat(pattern="yyyy-MM-dd")
//    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd",timezone = "GMT+8")
    @ApiModelProperty(value = "修改时间", name = "modifyTime", example = "2020-01-01")
    private Date modifyTime;
    @ApiModelProperty(value = "开始结束时间", name = "startTimeAndEndTime")
    private String[] startTimeAndEndTime;
    @DateTimeFormat(pattern="yyyy-MM-dd")
//    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd",timezone = "GMT+8")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "开始时间", name = "startTime", example = "2020-01-01")
    private String startTime;
    @DateTimeFormat(pattern="yyyy-MM-dd")
//    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd",timezone = "GMT+8")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "结束时间", name = "endTime", example = "2020-01-01")
    private String endTime;

}
