package com.yunya.modules.discount.enums;

import com.yunya.framework.common.model.*;

/**
 * @author xiangyang
 */

public enum DiscountError implements RestError {
    CARD_IS_GENERATED(30001, "产品已生成卡券"),
    FAIL_TO_GENERATE(30002, "卡券生成失败"),
    COUPON_NOT_ALLOCATE(30003, "优惠券未分配，请先分配再生成"),
    ORG_NOT_ALLOCATE(30004, "%s未分配优惠券，不能生成分配"),
    NUM_NOT_EQUAL(30005, "优惠券生成数量和分配数量不一致"),
    ORG_BATCH_ERROR(30006, "数据异常，%s不属于当前生成批次"),
    KEY_IS_LOCKED(30007, "卡券正在生成分配中，无法提交"),
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
