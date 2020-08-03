package com.yunya.modules.emr.enums;

import java.util.Objects;

/**
 * @author xiangyang
 */
public enum EnableEnum {
    DISABLE(0, "禁用"),
    ENABLE(1, "启用"),
    ;
    private final Integer code;
    private final String value;

    EnableEnum(Integer code, String value) {
        this.code = code;
        this.value = value;
    }

    public Integer getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }

    public static String getValue(Integer code)
    {
        if(code != null)
        {
            for(EnableEnum enable : values())
            {
                if(Objects.equals(enable.getCode(), code))
                {
                    return enable.getValue();
                }
            }
        }
        return null;
    }
}
