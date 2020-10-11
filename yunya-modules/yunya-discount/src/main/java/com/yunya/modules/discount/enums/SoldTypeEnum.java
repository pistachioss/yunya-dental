package com.yunya.modules.discount.enums;

import java.util.*;

public enum SoldTypeEnum {
    SOLD(0, "售出"),
    EXCHANGE(1, "置换"),
    PRESENTED(2, "赠送"),
    ;

    private Integer code;
    private String value;

    SoldTypeEnum(Integer code, String value) {
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
            for(SoldTypeEnum soldTypeEnum : values())
            {
                if(Objects.equals(soldTypeEnum.getCode(), code))
                {
                    return soldTypeEnum.getValue();
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
