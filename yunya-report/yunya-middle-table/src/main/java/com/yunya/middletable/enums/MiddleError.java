package com.yunya.middletable.enums;

import com.yunya.framework.common.constant.PreFixCode;
import com.yunya.framework.common.model.RestError;

public enum MiddleError implements RestError {
    DATE_ERROR(1, "日期错误"),
    BASE_COUPON_EXISTED(2, "优惠券基础表数据已存在[%s]"),
    BASE_COUPON_NOT_EXIST(3, "优惠券基础表数据不存在[%s]"),
    COUPON_NOT_EXIST(4, "优惠券源数据不存在[%s]"),
    COUPON_NOT_DELETED(5, "优惠券源数据未删除[%s]"),
    BASE_COUPON_ITEM_EXISTED(6, "优惠券项目基础表数据已存在[%s]"),
    BASE_COUPON_ITEM_NOT_EXISTED(7, "优惠券项目基础表数据不存在[%s]"),
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
