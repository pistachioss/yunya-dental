package com.yunya.modules.discount.enums;

import com.yunya.framework.common.constant.PreFixCode;
import com.yunya.framework.common.model.RestError;

/**
 * @author xiangyang
 */

public enum CouponOrderError implements RestError {
    COUPON_STOCK_LACK(1, "%s库存不足，请分配卡券"),
    SALE_CHANNEL_NULL(2, "销售渠道不存在"),
    COUPON_ORDER_ERROR(3, "订单异常"),
    COUPON_SOLD_ERROR(4, "下单失败"),
    CHANGE_ERROR(5, "非云牙系统购买卡券，不允许转赠"),
    CARD_ACTIVED(6, "已激活卡券，不允许转赠"),
    ;
    private Integer code;
    private String value;

    CouponOrderError(Integer code, String value) {
        this.code = code;
        this.value = value;
    }
    @Override
    public Integer getCode() {
        if (code.equals(0)) {
            return code;
        }
        return PreFixCode.COUPON_ORDER.getCode() * 1000 + code;
    }

    @Override
    public String getMessage() {
        return value;
    }
}
