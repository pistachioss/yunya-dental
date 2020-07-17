package com.yunya.employee.common.constant;

/**
 *
 */
public enum SexEnum {
    /**
     * 全职
     */
    FULLTIME(0, "男"),
    /**
     * 兼职
     */
    PARTIME(1, "女"),

    UN_KNOW(-1, "未知来源"),
    ;

    private final Integer code;
    private final String name;

    SexEnum(Integer code, String name)
    {
        this.code = code;
        this.name = name;
    }

    public Integer getCode()
    {
        return code;
    }

    public static String getName(Integer code) {
        for(SexEnum enable : values()) {
            if(enable.getCode().equals(code)) {
                return enable.name;
            }
        }
        return UN_KNOW.name;
    }
}
