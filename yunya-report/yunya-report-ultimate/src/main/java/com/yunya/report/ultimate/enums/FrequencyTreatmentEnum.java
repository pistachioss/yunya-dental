package com.yunya.report.ultimate.enums;

import com.yunya.framework.common.utils.NumberUtil;

/**
 * @author: chenlin
 * @date: 2023/5/15 17:17
 * @description: 诊疗频率枚举值
 * @since: 1.0.0
 */
public enum FrequencyTreatmentEnum {

    HIGH_FREQUENCY("高频", new Integer[]{0, 2}),
    MEDIUM_FREQUENCY("中频", new Integer[]{3, 5}),
    LOW_FREQUENCY("低频", new Integer[]{6, null}),
    ;

    /** 频率名称 */
    private String name;

    /** 频率范围（次数） */
    private Integer[] range;

    FrequencyTreatmentEnum(String name, Integer[] range) {
        this.name = name;
        this.range = range;
    }

    public static FrequencyTreatmentEnum getEnum(Integer dayOfSinceLastVisit) {
        for (FrequencyTreatmentEnum valEnum : values()) {
            Integer[] range = valEnum.getRange();
            if (NumberUtil.betweenAnd(dayOfSinceLastVisit, range)) {
                return valEnum;
            }
        }
        return null;
    }

    public Integer[] getRange() {
        return range;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
