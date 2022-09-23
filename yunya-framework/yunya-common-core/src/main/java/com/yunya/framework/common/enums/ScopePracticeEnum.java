package com.yunya.framework.common.enums;

import java.util.Objects;

/**
 *  职业范围
 * @author xiangyang
 * @date 2020/9/17
 */
public enum ScopePracticeEnum {
    /**
     * 口腔
     */
    SCOPE_007("007", "口腔"),
    /**
     * 医学影像和放射治疗
     */
    SCOPE_012("012", "医学影像和放射治疗"),
    ;

    private String code;
    private String value;

    ScopePracticeEnum(String code, String value) {
        this.code = code;
        this.value = value;
    }

    public String getCode() {
        return code;
    }
    public String getValue() {
        return value;
    }

    /**
     * 根据code获取value
     * @param code code
     * @return value
     */
    public static String getValue(String code)
    {
        if(code != null)
        {
            for(ScopePracticeEnum useWayEnum : values())
            {
                if(Objects.equals(useWayEnum.getCode(), code))
                {
                    return useWayEnum.getValue();
                }
            }
        }
        return null;
    }

    public boolean equals(String code)
    {
        return this.code.equals(code);
    }
}
