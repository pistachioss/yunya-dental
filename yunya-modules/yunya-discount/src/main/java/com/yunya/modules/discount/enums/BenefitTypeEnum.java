package com.yunya.modules.discount.enums;

import java.util.Objects;

/**
 * @author xiangyang
 * @date 2020/9/17
 */
public enum BenefitTypeEnum {
    /**
     * 会员卡优惠
     */
    MEMBER_TYPE(0, "会员卡优惠"),
    /**
     * 优惠券优惠
     */
    COUPON_TYPE(1, "优惠券优惠"),
    ;

    private Integer code;
    private String value;

    BenefitTypeEnum(Integer code, String value) {
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
            for(BenefitTypeEnum useWayEnum : values())
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
