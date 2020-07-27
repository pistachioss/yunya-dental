package com.yunya.modules.employee.expand.constant;

/**
 * @author bruce
 */

public enum TrueOrFalseEnum
{
    TRUE(1, "是"),

    FALSE(0, "否");
    private Integer code;
    private String name;

    TrueOrFalseEnum(Integer code, String name)
    {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }
    public static String getName(Integer code) {
        for(TrueOrFalseEnum enable : values()) {
            if(enable.getCode().equals(code)) {
                return enable.name;
            }
        }
        return null;
    }
}
