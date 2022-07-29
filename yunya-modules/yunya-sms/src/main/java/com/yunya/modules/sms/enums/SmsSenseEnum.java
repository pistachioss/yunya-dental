package com.yunya.modules.sms.enums;

import java.util.Objects;

/**
 * 简介：适用场景
 *
 * @author: chenlin
 * @Description:
 * @Date: 2020/11/12 16:15
 * @since: 1.0.0
 */
public enum SmsSenseEnum {
    APPOINTMENT_REMINDER((byte)0, "预约提醒"),
    MEMBER_CHARGE_REMINDER((byte)1, "会员充值提醒"),
    MEMBER_CONSUME_REMINDER((byte)2, "会员消费提醒"),
    PRE_CHARGE_REMINDER((byte)3, "预付款充值提醒"),
    PRE_CONSUME_REMINDER((byte)4, "预付款消费提醒"),
    COUPON_SALE_REMINDER((byte)5, "卡券售出提醒"),
    DEVICE_BINDING_VERIFYCODE((byte)6, "考勤设备绑定验证码"),
    RETRIEVE_PWD_VERIFYCODE((byte)7, "找回密码验证码"),
    COUPON_ACTIVED((byte)8, "卡券激活");

    private final Byte code;
    private final String value;

    SmsSenseEnum(Byte code, String value) {
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
            for(SmsSenseEnum item : values())
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
