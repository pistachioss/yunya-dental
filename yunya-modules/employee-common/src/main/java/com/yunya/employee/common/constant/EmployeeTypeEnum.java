package com.yunya.employee.common.constant;

/**
 *
 */
public enum EmployeeTypeEnum {
    /**
     * 试用期
     */
    TRIAL(0, "试用"),
    /**
     * 转正期
     */
    REGULAR(1, "转正"),
    /**
     * 离职期
     */
    LEAVING(2, "离职"),
    UN_KNOW(-1, "未知来源"),
    ;
    private final Integer code;
    private final String name;

    EmployeeTypeEnum(Integer code, String name)
    {
        this.code = code;
        this.name = name;
    }

    public Integer getCode()
    {
        return code;
    }

    public static String getName(Integer code) {
        for(EmployeeTypeEnum enable : values()) {
            if(enable.getCode().equals(code)) {
                return enable.name;
            }
        }
        return UN_KNOW.name;
    }
}
