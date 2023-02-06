package com.yunya.modules.system.enums;

import java.util.Objects;

/**
 * @description: 入账方式类型枚举
 * @author: chenlin
 * @date: 2023-02-05 10:53
 */
public enum AccountItemTypeEnum {

    CASH("现金", (byte)0),
    PRE_SOLE("预售", (byte)1),
    DISCOUNT("优惠", (byte)2),
    PLATFORM_SETTLEMENT("平台结算", (byte)3),
    ;

    private final String value;
    private final Byte type;

    AccountItemTypeEnum(String value, Byte code) {
        this.value = value;
        this.type = code;
    }

    public String getValue() {
        return value;
    }

    public Byte getType() {
        return type;
    }

    public static String getValue(Byte type) {
        if (type != null) {
            for (AccountItemTypeEnum item : values()) {
                if (Objects.equals(item.getType(), type)) {
                    return item.getValue();
                }
            }
        }
        return null;
    }

    public boolean equals(Byte type)
    {
        return this.type.equals(type);
    }
}
