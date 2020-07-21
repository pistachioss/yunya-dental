package com.yunya.framework.common.enums;

/**
 * @author bruce
 */
public enum EmployeeWorkStatuEnum {
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

    EmployeeWorkStatuEnum(Integer code, String name)
    {
        this.code = code;
        this.name = name;
    }

    public Integer getCode()
    {
        return code;
    }

    public static String getName(Integer code) {
        for(EmployeeWorkStatuEnum enable : values()) {
            if(enable.getCode().equals(code)) {
                return enable.name;
            }
        }
        return UN_KNOW.name;
    }
}
