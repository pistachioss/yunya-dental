package com.yunya.modules.sms.enums;

import java.util.Objects;

/**
 * 简介：订单状态
 *
 * @author: chenlin
 * @Description:
 * @Date: 2020/11/12 16:15
 * @since: 1.0.0
 */
public enum SmsOrderStatusEnum {
    WAIT_PAY((byte)0, "等待付款"),
    PAY_SUC((byte)1, "付款成功"),
    PAY_FAIL((byte)2, "付款失败"),
    CLOSED((byte)3, "已关闭");

    private final Byte code;
    private final String value;

    SmsOrderStatusEnum(Byte code, String value) {
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
            for(SmsOrderStatusEnum item : values())
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
