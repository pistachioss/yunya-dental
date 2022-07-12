package com.yunya.framework.common.enums;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 简介：短信自动发送事件枚举
 *
 * @author: chenlin
 * @Description:
 * @Date: 2020/12/12 16:15
 * @since: 1.0.0
 */
public enum SmsAutosendEventEnum {
    MEMBER_CHARGE(2, "会员充值", "member_charge"),
    MEMBER_CONSUME(2, "会员消费", "member_consume"),
    PREPAY_CHARGE(2, "预付款充值", "prepay_charge"),
    PREPAY_CONSUME(2, "预付款消费", "prepay_consume"),
//    BIRTHDAY_WISH(2, "生日祝福", "birthday_wish"),
//    PRODUCT_EXPIRATION_REMIND(2, "产品到期提醒", "product_expiration_remind"),
    SP_COUPON_ACTIVED(2, "365卡or艾芽卡券激活", "sp_coupon_actived"),
    COUPON_SOLD(0, "卡券售出", "coupon_sold"),
    FORGET_PASSWORD(1, "找回密码", "forget_password"),
    ATTENDANCE_DEVICE_BINDING(1, "考勤设备绑定", "attendance_device_binding");

    /*类型：0-公共，1-公司，2-门诊*/
    private final Integer type;
    private final String value;
    private final String code;

    SmsAutosendEventEnum(Integer type, String value, String code) {
        this.type = type;
        this.value = value;
        this.code = code;
    }

    public static List<SmsAutosendEventEnum> values(boolean isClinic) {
        List<SmsAutosendEventEnum> companyEvents = new ArrayList<>();
        List<SmsAutosendEventEnum> clinicEvents = new ArrayList<>();
        SmsAutosendEventEnum[] smsAutosendEventEnums = values();
        for (SmsAutosendEventEnum smsAutosendEventEnum : smsAutosendEventEnums) {
            Integer type = smsAutosendEventEnum.getType();
            switch (type) {
                case 0:{
                    companyEvents.add(smsAutosendEventEnum);
                    clinicEvents.add(smsAutosendEventEnum);
                    break;
                }
                case 1:{
                    companyEvents.add(smsAutosendEventEnum);
                    break;
                }
                case 2:{
                    clinicEvents.add(smsAutosendEventEnum);
                    break;
                }
                default:
            }
        }
        return isClinic?clinicEvents:companyEvents;
    }

    public String getValue() {
        return value;
    }

    public Integer getType() {
        return type;
    }

    public String getCode() {
        return code;
    }

    public String getValueByCode(Integer code) {
        if (code != null) {
            for (SmsAutosendEventEnum item : values()) {
                if (Objects.equals(item.getType(), code)) {
                    return item.getValue();
                }
            }
        }
        return null;
    }

    public boolean equals(Integer code)
    {
        return this.type.equals(code);
    }
}
