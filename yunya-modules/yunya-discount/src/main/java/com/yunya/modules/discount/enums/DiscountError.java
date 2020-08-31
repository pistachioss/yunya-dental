package com.yunya.modules.discount.enums;

import com.yunya.framework.common.model.*;

/**
 * @author xiangyang
 */

public enum DiscountError implements RestError {
    CARD_IS_GENERATED(30001, "产品已生成卡券"),
    FAIL_TO_GENERATE(30002, "卡券生成失败"),
    COUPON_NOT_ALLOCATE(30003, "优惠券未分配，请先分配再生成"),
    ORG_NOT_ALLOCATE(30004, "%s未分配该优惠券，不能生成分配"),
    NUM_NOT_EQUAL(30005, "优惠券生成数量和分配数量不一致"),
    ORG_BATCH_ERROR(30006, "数据异常，%s不属于当前生成批次"),
    COUPON_IS_LOCKED(30007, "卡券正在生成分配中，无法提交"),
    CARD_SOLD_IS_LOCKED(30008, "卡券正在售卖中，无法提交"),
    COUPON_NOT_EXIST(30009, "优惠券不存在"),
    SOLD_DATE_RANGE_ERROR(30010, "卡券不在售出时间范围内"),
    ORG_COUPON_NOT_ALLOCATE(30011, "优惠券%s，%s未生成分配"),
    CARD_NOT_EXIST(30012, "卡券不存在"),
    CARD_SOLD_STATUS_ERROR(30013, "卡券售卖状态异常"),
    CARD_SOLD_OUT(30014, "%s的%s已全部售出"),
    CARD_NOT_BELONG_ORG(30016, "该卡券不属于%s"),
    CARD_NOT_BELONG_COUPON(30017, "卡券是%s，请重新选择优惠券类型"),
    CARD_ACTIVE_IS_LOCKED(30018, "卡券正在激活中，无法提交"),
    CARD_IS_ACTIVATED(30019, "该卡券已被激活"),
    CARD_ACTIVE_STATUS_ERROR(30020, "该卡券不是待激活状态，无法激活"),
    CARD_BEYOND_DEADLINE(30021, "优惠券已超过有效期"),
    CARD_NOT_CHARGE(30022, "卡券未收费"),
    CARD_IS_CHARGED(30023, "卡券已收费"),
    CARD_IS_EXIST(30024, "卡券已存在"),
    CARD_NOT_ACTIVATED(30025, "卡券未激活"),
    COUPON_NOT_ALLOW_SHARE(30026, "该优惠券不能与他人共享"),
    SHARER_NOT_ALLOW_OWNER(30027, "配置共享人不能是患者自己"),
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
