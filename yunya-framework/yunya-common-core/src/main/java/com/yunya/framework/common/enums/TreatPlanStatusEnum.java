package com.yunya.framework.common.enums;

import java.util.Objects;

/**
 * 简介：治疗计划状态枚举
 *
 * @author: chenlin
 * @Description:
 * @Date: 2021/10/19 16:15
 * @since: 1.0.0
 */
public enum TreatPlanStatusEnum {
    UNCONFIRM("未确认", 0),
    CONFIRMED("已确认", 1),
    EXECUTING("进行中", 2),
    COMPLETED("全部完成", 3),
    TERMINATION("提前终止", 4);

    private final String value;
    private final Integer code;

    TreatPlanStatusEnum(String value, Integer code) {
        this.value = value;
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public Integer getCode() {
        return code;
    }

    public static String getValue(Integer code) {
        if (code != null) {
            for (TreatPlanStatusEnum item : values()) {
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
