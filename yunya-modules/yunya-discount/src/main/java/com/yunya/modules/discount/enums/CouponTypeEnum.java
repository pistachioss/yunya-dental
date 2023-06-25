package com.yunya.modules.discount.enums;


import java.util.*;

public enum CouponTypeEnum {
    VOUCHER(0, "代金券"),
    DISCOUNT(1, "折扣券"),
    EXCHANGE(2, "兑换券"),
    SPECIAL_PACKAGE(3, "套餐券"),
    RECHARGE(4, "充值卡"),
    MEMBER_CARD(99, "会员卡"),
    DEDUCTION(5, "划扣券"),
    ;

    private Integer code;
    private String value;

    CouponTypeEnum(Integer code, String value) {
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
            for(CouponTypeEnum couponTypeEnum : values())
            {
                if(Objects.equals(couponTypeEnum.getCode(), code))
                {
                    return couponTypeEnum.getValue();
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
