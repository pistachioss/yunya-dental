package com.yunya.framework.common.constant;

public enum PreFixCode {
    ADMIN_SYSTEM(10),
    APPOINTMENT(11),
    CLINIC_BASE(12),
    DISCOUNT(13),
    EMPLOYEE_ATTEND(14),
    EMPLOYEE_EXPAND(15),
    EMR(16),
    PATIENT(17),
    TARIFF(18),
    TREATMENT(19),
    TREATMENT_OTHER(20),
    ;


    private Integer code;

    PreFixCode(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
