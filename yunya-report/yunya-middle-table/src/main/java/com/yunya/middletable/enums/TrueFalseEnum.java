package com.yunya.middletable.enums;

import java.util.Objects;

public enum TrueFalseEnum {
    FALSE(0, "否"),
    TRUE(1, "是"),
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

    /**
     * 根据code获取value
     * @param code code
     * @return value
     */
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
