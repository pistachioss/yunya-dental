package com.yunya.modules.discount.enums;

import java.util.*;

/**
 * @author xiangyang
 */

public enum CardStatusEnum {
    SALE_PENDING(0, "生成"),
    ACTIVE_PENDING(1, "售出"),
    ACTIVATED(2, "已激活"),
    ;

    private Integer code;
    private String value;

    CardStatusEnum(Integer code, String value) {
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
            for(CardStatusEnum cardEnum : values())
            {
                if(Objects.equals(cardEnum.getCode(), code))
                {
                    return cardEnum.getValue();
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
