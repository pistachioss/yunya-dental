package com.yunya.framework.common.enums;

import java.util.Objects;

/**
 * 简介：写操作类型枚举
 *
 * @author: chenlin
 * @Description:
 * @Date: 2021/10/19 16:15
 * @since: 1.0.0
 */
public enum OperationTypeEnum {
    INSERT("新增", (byte)0),
    UPDATE("修改", (byte)1),
    DELETE("删除", (byte)2);

    private final String value;
    private final Byte code;

    OperationTypeEnum(String value, Byte code) {
        this.value = value;
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public Byte getCode() {
        return code;
    }

    public static String getValue(Integer code) {
        if (code != null) {
            for (OperationTypeEnum item : values()) {
                if (Objects.equals(item.getCode(), code)) {
                    return item.getValue();
                }
            }
        }
        return null;
    }

    public boolean equals(Integer code)
    {
        return this.code.equals(code);
    }
}
