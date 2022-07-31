package com.sz.commonutils.Result;

import lombok.Data;

@Data
public class Result<T> {
    private Integer code;

    private String message;

    private T data;

    public Result(){}

    protected static <T> Result<T> build(T data){
        Result<T> result = new Result<>();
        if(data != null) result.setData(data);
        return result;
    }

    public static <T> Result<T> build(T data, ResultCode resultCode){
        Result<T> result = new Result<>();
        if(data != null) result.setData(data);
        if(resultCode != null){
            result.setMessage(resultCode.getMessage());
            result.setCode(resultCode.getCode());
        }
        return result;
    }

    public static <T> Result<T> ok(T data){
        return build(data, ResultCode.SUCCESS);
    }

    public static <T> Result<T> ok(){
        return build(null, ResultCode.SUCCESS);
    }

    public static <T> Result<T> fail(T data){
        return build(data, ResultCode.FAIL);
    }

    public static <T> Result<T> fail(){
        return build(null, ResultCode.FAIL);
    }
}
