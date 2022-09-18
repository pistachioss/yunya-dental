package com.yunya.modules.discount.enums;

import java.util.*;

public enum SoldWayEnum {
    ON_LINE(0, "线上"),
    OFF_LINE(1, "线下"),
    MINI_ORDER(2, "小程序订单"),
    ;

    private Integer code;
    private String value;

    SoldWayEnum(Integer code, String value) {
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
            for(SoldWayEnum soldWayEnum : values())
            {
                if(Objects.equals(soldWayEnum.getCode(), code))
                {
                    return soldWayEnum.getValue();
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
