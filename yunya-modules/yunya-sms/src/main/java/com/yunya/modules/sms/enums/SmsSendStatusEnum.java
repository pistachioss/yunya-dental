package com.yunya.modules.sms.enums;

import java.util.Objects;

/**
 * 简介：阿里云短信发送状态
 *
 * @author: chenlin
 * @Description:
 * @Date: 2020/11/12 16:15
 * @since: 1.0.0
 */
public enum SmsSendStatusEnum {
    SENDING((byte)0, "发送中"),
    SEND_SUCC((byte)1, "发送成功"),
    SEND_FAIL((byte)2, "发送失败");

    private final Byte code;
    private final String value;

    SmsSendStatusEnum(Byte code, String value) {
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
            for(SmsSendStatusEnum item : values())
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
