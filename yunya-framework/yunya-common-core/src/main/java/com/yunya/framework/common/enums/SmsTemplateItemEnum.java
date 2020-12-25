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
    VERIFY_CODE(0, "验证码", "code"),
    PATIENT_NAME(1, "患者姓名", "patient_name"),
    CLINIC_NAME(2, "诊所名称", "clinic_name"),
    CLINIC_PHONE(3, "诊所电话", "clinic_phone"),
    CLINIC_ADDRESS(4, "诊所地址", "clinic_address"),
    APPOINTMENT_DOCTOR(5, "预约医生姓名", "appointment_doctor"),
    APPOINTMENT(6, "预约时间", "appointment"),
    APPELLATION(7, "先生/女士/小朋友", "appellation"),
    TODAY_TOMORROW(8, "今天/明天", "today_tomorrow"),
    MORNING_AFTERNOON(9, "上午/下午", "morning_afternoon"),
    MEMBER_RECHARGE_AMOUNT(10, "会员充值金额", "member_recharge_amount"),
    MEMBER_SPENDING_AMOUNT(11, "会员消费金额", "member_spending_amount"),
    MEMBER_REMAINING_AMOUNT(12, "会员剩余金额", "member_remaining_amount"),
    MEMBER_CARD_NUMBER(13, "会员卡号", "member_card_number"),
    PREPAID_RECHARGE_AMOUNT(14, "预付款充值金额", "prepaid_recharge_amount"),
    PREPAID_CONSUMPTION_AMOUNT(15, "预付款消费金额", "prepaid_consumption_amount"),
    PREPAID_REMAINING_AMOUNT(16, "预付款剩余金额", "prepaid_remaining_amount"),
    PREPAID_ACCOUNT(17, "预付款账号", "prepaid_account"),
    PRODUCT_MODEL(18, "产品型号", "product_model"),
    PRODUCT_NAME(19, "产品名称", "product_name"),
    COUPON_CARD_NUMBER(20, "卡券卡号", "coupon_card_number"),
    COUPON_CARD_SECRET(21, "卡券卡密", "coupon_card_secret");

    private final Integer code;
    private final String value;
    private final String action;

    private static final Map<String, String> actions = new HashMap<>();

    static {
        synchronized (SmsTemplateItemEnum.class) {
            for (SmsTemplateItemEnum value : values()) {
                actions.put(value.getCode() + "", value.getAction());
            }
        }
    }

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

    public static String getAction(String code) {
        return actions.get(code);
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
