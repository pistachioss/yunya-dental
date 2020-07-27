package com.yunya.modules.employee.expand.constant;

/**
 *
 */
public enum EmployeeWorkEnum {
    /**
     * 全职
     */
    FULLTIME(0, "全职"),
    /**
     * 兼职
     */
    PARTIME(1, "转正"),

    UN_KNOW(-1, "未知来源"),
    ;

    private final Integer code;
    private final String name;

    EmployeeWorkEnum(Integer code, String name)
    {
        this.code = code;
        this.name = name;
    }

    public Integer getCode()
    {
        return code;
    }

    public static String getName(Integer code) {
        for(EmployeeWorkEnum enable : values()) {
            if(enable.getCode().equals(code)) {
                return enable.name;
            }
        }
        return UN_KNOW.name;
    }
}
