package com.sz.commonutils.Result;

import lombok.Data;
import lombok.Getter;

@Getter
public enum ResultCode {
    SUCCESS("成功", 200),
    NOT_FOUND("未找到", 404),
    INTERNAL_ERROR("内部错误", 500),
    FAIL("失败", 400);
    private String message;
    private int code;

    private ResultCode(String message, int code) {
        this.code = code;
        this.message = message;
    }
}
