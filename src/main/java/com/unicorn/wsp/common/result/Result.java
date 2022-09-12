package com.unicorn.wsp.common.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.http.HttpStatus;

/**
 * @author zy
 */
@Data
@ApiModel(value="Result",description="Result")
public class Result {
    @ApiModelProperty(value="code",name="code",example="0")
    private Integer resultCode;
    @ApiModelProperty(value="msg",name="msg",example="success")
    private String message;
    @ApiModelProperty(value="data",name="data",example="{}")
    private Object data;

    public Result() {
    }

    public Result(Integer code, String message) {
        this.resultCode = code;
        this.message = message;
    }

    public Result error(){
        return this.render(ResultEnum.error.getCode(),"error");

    }
    public Result error(HttpStatus status, String msg){
        return this.render(status.value(),msg);
    }

    public Result error(String msg){
        return this.render(ResultEnum.error.getCode(),msg);
    }

    public Result fail(String msg){
        return this.render(ResultEnum.fail.getCode(),msg);
    }

    public Result fail(){
        return this.render(ResultEnum.fail.getCode(),"操作失败");
    }

    public Result fail(Object data){
        return this.render(ResultEnum.fail.getCode(),"操作失败",data);
    }

    public Result success(){
        return this.render(ResultEnum.success.getCode(),"操作成功");
    }
    public Result success(Object data){
        return this.render(ResultEnum.success.getCode(),"操作成功",data);
    }
    public Result success(String msg){
       return this.render(ResultEnum.success.getCode(),msg);
    }

    public Result success(Integer code,Object data){
        return this.render(code,"操作成功",data);
    }

    public Result render(Integer code, String msg){
        this.resultCode = code;
        this.message = msg;
        return this;
    }

    public Result render(Integer ret, String msg, Object data){
        this.resultCode = ret;
        this.message = msg;
        this.data = data;
        return this;
    }
}
