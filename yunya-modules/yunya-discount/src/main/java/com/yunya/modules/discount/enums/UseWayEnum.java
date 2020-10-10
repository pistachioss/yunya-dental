package com.yunya.modules.discount.enums;

import java.util.*;

/**
 * @author xiangyang
 * @date 2020/8/31
 */
public enum  UseWayEnum {
    one_time_use(0, "一次使用"),
    many_time_use(1, "多次使用"),
    ;

    private Integer code;
    private String value;

    UseWayEnum(Integer code, String value) {
        this.code = code;
        this.value = value;
    }

    public Integer getCode() {
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
    public static String getValue(Integer code)
    {
        if(code != null)
        {
            for(UseWayEnum useWayEnum : values())
            {
                if(Objects.equals(useWayEnum.getCode(), code))
                {
                    return useWayEnum.getValue();
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
