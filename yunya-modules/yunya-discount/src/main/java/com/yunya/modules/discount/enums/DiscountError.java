package com.yunya.modules.discount.enums;

import com.yunya.framework.common.model.*;

public enum DiscountError implements RestError {
    CARD_IS_GENERATED(30001, "产品已生成卡券"),
    ;
    private Integer code;
    private String value;

    DiscountError(Integer code, String value) {
        this.code = code;
        this.value = value;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return value;
    }
}
