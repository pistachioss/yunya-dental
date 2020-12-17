package com.yunya.modules.sms.enums;

import java.util.Objects;

/**
 * 简介：阿里云短信模板类型
 *
 * @author: chenlin
 * @Description:
 * @Date: 2020/11/12 16:15
 * @since: 1.0.0
 */
public enum SmsTypeEnum {
    VERIFY_CODE((byte)0, "验证码"),
    SMS_NOTIFY((byte)1, "短信通知"),
    SMS_PROMOTE((byte)2, "推广短信"),
    INTERNAT_SMS((byte)3, "国际/港澳台消息");

    private final Byte code;
    private final String value;

    SmsTypeEnum(Byte code, String value) {
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
            for(SmsTypeEnum item : values())
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
