package com.yunya.framework.common.enums;

import java.util.Objects;

/**
 * 简介：文件来源类型枚举
 *
 * @author: chenlin
 * @Description:
 * @Date: 2021/10/19 16:15
 * @since: 1.0.0
 */
public enum FileSourceTypeEnum {
    OTHER("其他", (byte)0),
    TREAT_PLAN("治疗计划", (byte)1),
    MEDICAL_COMMON("普通电子病历", (byte)2),
    PATIENT_SIGNATURE("患者签名", (byte)3),
    PATIENT_EMR_PIC("患者病历照片", (byte)4);

    private final String value;
    private final Byte code;

    FileSourceTypeEnum(String value, Byte code) {
        this.value = value;
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public Byte getCode() {
        return code;
    }

    public static String getValue(Integer code) {
        if (code != null) {
            for (FileSourceTypeEnum item : values()) {
                if (Objects.equals(item.getCode(), code)) {
                    return item.getValue();
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
