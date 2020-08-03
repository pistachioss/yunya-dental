package com.yunya.modules.emr.enums;

/**
 * @author xiangyang
 */

public enum EventTypeEnum {
    DRAFT_AUDIT(0, "草稿病历审批"),
    MEDICAL_CHANGE_AUDIT(1, "病例变更审批"),
    ;

    private final Integer code;
    private final String value;

    EventTypeEnum(Integer code, String value) {
        this.code = code;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public Integer getCode() {
        return code;
    }
}
