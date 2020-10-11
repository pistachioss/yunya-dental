package com.yunya.modules.discount.enums;

import java.util.Objects;

public enum BenefitOperateEnum {
    CHARGE(1, "收费"),
    modify_bill(2, "撤销账单"),

    ;

    private Integer code;
    private String value;

    BenefitOperateEnum(Integer code, String value) {
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
            for(BenefitOperateEnum benefitOperateEnum : values())
            {
                if(Objects.equals(benefitOperateEnum.getCode(), code))
                {
                    return benefitOperateEnum.getValue();
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
