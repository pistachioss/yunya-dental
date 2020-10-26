package com.yunya.middletable.enums;

import com.yunya.framework.common.constant.PreFixCode;
import com.yunya.framework.common.model.RestError;

public enum MiddleError implements RestError {
    DATE_ERROR(1, "日期错误"),
    ;
    private Integer code;
    private String value;

    MiddleError(Integer code, String value) {
        this.code = code;
        this.value = value;
    }
    @Override
    public Integer getCode() {
        return PreFixCode.MIDDLE_TABLE.getCode() * 1000 + code;
    }

    @Override
    public String getMessage() {
        return value;
    }
}
