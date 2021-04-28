package com.yunya.feign.wechat.enums;

/**
 * @description:
 * @author: xy
 * @date 2021/4/19 15:46
 **/
public enum TemplateDataEnum {
    PATIENT_NAME("patientName"),
    APPOINT_DATE("appointDate"),
    COUPON_NAME("couponName"),
    LINK_MOBILE("linkMobile"),
    APPOINT_ID("appointId"),
    ;

    private String argName;

    public String getArgName() {
        return argName;
    }

    TemplateDataEnum(String argName) {
        this.argName = argName;
    }

}
