package com.yunya.modules.discount.enums;

import java.util.*;

public enum TrueFalseEnum {
    ON_LINE(0, "否"),
    OFF_LINE(1, "是"),
    ;

    private Integer code;
    private String value;

    TrueFalseEnum(Integer code, String value) {
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
            for(TrueFalseEnum trueFalseEnum : values())
            {
                if(Objects.equals(trueFalseEnum.getCode(), code))
                {
                    return trueFalseEnum.getValue();
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
