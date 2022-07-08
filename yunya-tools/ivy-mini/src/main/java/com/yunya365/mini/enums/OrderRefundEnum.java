package com.yunya365.mini.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * 申请状态：0->待处理；1->退货中；2->已完成；3->已拒绝
 * @author xiangyang
 */

public enum OrderRefundEnum {
    /**
     * 待处理
     */
    HANDLE_PENDING(0, "待处理"),
    /**
     * 退货中
     */
    REFUNDING(1, "退货"),
    /**
     * 已拒绝
     */
    REFUND_REFUSE(2, "已拒绝"),
    ;

    private final Integer code;
    private final String value;

    OrderRefundEnum(Integer code, String value) {
        this.code = code;
        this.value = value;
    }

    /**
     * 解析类型
     *
     * @param value value
     * @return LoginEnum
     */
    public static OrderRefundEnum resolveEvent(String value) {
        for (OrderRefundEnum loginEnum : OrderRefundEnum.values()) {
            if (StringUtils.equals(value, loginEnum.getValue())) {
                return loginEnum;
            }
        }
        return null;
    }

    public Integer getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }

}
