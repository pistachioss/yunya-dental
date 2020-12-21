package com.yunya.framework.common.enums;

import java.util.*;

/**
 * 简介：短信模板的变量
 *
 * @author: chenlin
 * @Description:
 * @Date: 2020/11/12 16:15
 * @since: 1.0.0
 */
public enum SmsTemplateItemEnum {
    VERIFY_CODE(0, "验证码", ""),
    PATIENT_NAME(1, "患者姓名", ""),
    CLINIC_NAME(2, "诊所名称", ""),
    CLINIC_PHONE(3, "诊所电话", ""),
    CLINIC_ADDRESS(4, "诊所地址", ""),
    APPOINTMENT_DOCTOR(5, "预约医生姓名", ""),
    APPOINTMENT(6, "预约时间", ""),
    MR_MS_CHILDREN(7, "先生/女士/小朋友", ""),
    TODAY_TOMORROW(8, "今天/明天", ""),
    ONDUTY_OFFDUTY(9, "上午/下午", ""),
    MEMBER_RECHARGE_AMOUNT(10, "会员充值金额", ""),
    MEMBER_SPENDING_AMOUNT(11, "会员消费金额", ""),
    MEMBER_SURPLUS_AMOUNT(12, "会员剩余金额", ""),
    MEMBER_CARD_NUMBER(13, "会员卡号", ""),
    PRE_CHARGE_AMOUNT(14, "预付款充值金额", ""),
    PRE_CONSUM_AMOUNT(15, "预付款消费金额", ""),
    PRE_SURPLUS_AMOUNT(16, "预付款剩余金额", ""),
    PRE_ACCOUNT(17, "预付款账号", ""),
    PRODUCT_MODEL(18, "产品型号", ""),
    PRODUCT_NAME(19, "产品名称", ""),
    COUPON_CARD_NUMBER(20, "卡券卡号", ""),
    COUPON_CARD_SECRET(21, "卡券卡密", "");

    private final Integer code;
    private final String value;
    private final String action;

    SmsTemplateItemEnum(Integer code, String value, String action) {
        this.code = code;
        this.value = value;
        this.action = action;
    }

    public static List<String> toList(String head, String tail) {
        List<String> list = new ArrayList<>();
        for(SmsTemplateItemEnum item : values()) {
            String value = item.getValue();
            if (!list.contains(value)) {
                list.add(head + value + tail);
            }
        }
        return list;
    }

    public String getValue() {
        return value;
    }

    public Integer getCode() {
        return code;
    }

    public String getAction() {
        return action;
    }

    public static String getValue(Integer code) {
        if(code != null) {
            for(SmsTemplateItemEnum item : values()) {
                if(Objects.equals(item.getCode(), code)) {
                    return item.getValue();
                }
            }
        }
        return null;
    }

    /**
     * 获取<action, [code1,code2...]>
     * @param codes
     * @return
     */
    public static Map<String, List<String>> getActions(String... codes) {
        Map<String, List<String>> actions = new HashMap<>();
        for (int i = 0; i < codes.length; i++) {
            Integer code = Integer.parseInt(codes[i]);
            if(code != null) {
                for(SmsTemplateItemEnum item : values()) {
                    if(Objects.equals(item.getCode(), code)) {
                        String action = item.getAction();
                        List<String> list = actions.get(action);
                        if (list == null) {
                            list = new ArrayList<>();
                        }
                        list.add("code" + code);
                        actions.put(action, list);
                    }
                }
            }
        }
        return actions;
    }

    public static String getAction(Integer code) {
        if(code != null) {
            for(SmsTemplateItemEnum item : values())
            {
                if(Objects.equals(item.getCode(), code))
                {
                    return item.getAction();
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
