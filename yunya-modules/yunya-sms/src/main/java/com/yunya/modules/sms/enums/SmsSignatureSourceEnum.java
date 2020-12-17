package com.yunya.modules.sms.enums;

import java.util.Objects;

/**
 * 简介：阿里云签名来源
 *
 * @author: chenlin
 * @Description:
 * @Date: 2020/11/12 16:15
 * @since: 1.0.0
 */
public enum SmsSignatureSourceEnum {
    ENTERPRISE((byte)0, "企事业单位的全称或简称"),
    RECORD_WEBSITE((byte)1, "工信部备案网站的全称或简称"),
    APPLICATION((byte)2, "APP应用的全称或简称"),
    APPLETS((byte)3, "公众号或小程序的全称或简称"),
    ELECBIZ((byte)3, "电商平台店铺名的全称或简称"),
    TRADE((byte)3, "商标名的全称或简称");

    private final Byte code;
    private final String value;

    SmsSignatureSourceEnum(Byte code, String value) {
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
            for(SmsSignatureSourceEnum item : values())
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
