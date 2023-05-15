package com.yunya.report.ultimate.enums;

import com.yunya.framework.common.utils.NumberUtil;

/**
 * @author: chenlin
 * @date: 2023/5/15 13:23
 * @description: 患者活跃度枚举
 * @since: 1.0.0
 */
public enum ActivityDegreeEnum {

    ACTIVE("活跃", new Integer[]{1, 180}),
    INACTIVITY("不活跃", new Integer[]{181, 360}),
    SLEEPING("沉睡客户", new Integer[]{361, 540}),
    HIBERNATION("休眠客户", new Integer[]{541, null}),
    ;


    /** 活跃度名称 */
    private String name;
    
    /** 范围（天数） */
    private Integer[] range;

    ActivityDegreeEnum(String name, Integer[] range) {
        this.name = name;
        this.range = range;
    }

    public static ActivityDegreeEnum getEnum(Integer dayOfSinceLastVisit) {
        for (ActivityDegreeEnum valEnum : ActivityDegreeEnum.values()) {
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
