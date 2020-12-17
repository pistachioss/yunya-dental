package com.yunya.modules.sms.enums;

import java.util.Objects;

/**
 * 简介：启用、禁用状态
 *
 * @author: chenlin
 * @Description:
 * @Date: 2020/11/12 16:15
 * @since: 1.0.0
 */
public enum SmsEnableEnum {
    DISABLE((byte)0, "禁用"),
    ENABLE((byte)1, "启用");

    private final Byte code;
    private final String value;

    SmsEnableEnum(Byte code, String value) {
        this.code = code;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public Byte getCode() {
        return code;
    }

    public static String getValue(Byte code)
    {
        if(code != null)
        {
            for(SmsEnableEnum item : values())
            {
                if(Objects.equals(item.getCode(), code))
                {
                    return item.getValue();
                }
            }
        }
        return null;
    }

    public boolean equals(Byte code)
    {
        return this.code.equals(code);
    }
}
