package com.yunya365.mini.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * 0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->申请退款',
 * @author xiangyang
 */

public enum OrderStatusEnum {
    /**
     * 待付款
     */
    PAY_PENDING(0, "待付款"),
    /**
     * 待发货
     */
    SHIP_PENDING(1, "待发货"),
    /**
     * 已发货
     */
    HAS_SHIP(2, "已发货"),
    /**
     * 已完成
     */
    FINISH(3, "已完成"),
    /**
     * 已关闭
     */
    CLOSE(4, "已关闭"),
    /**
     * 申请退款
     */
    APPLY_REFUND(5, "申请退款"),
    ;

    private final Integer code;
    private final String value;

    OrderStatusEnum(Integer code, String value) {
        this.code = code;
        this.value = value;
    }

    /**
     * 解析类型
     *
     * @param value value
     * @return LoginEnum
     */
    public static OrderStatusEnum resolveEvent(String value) {
        for (OrderStatusEnum loginEnum : OrderStatusEnum.values()) {
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
