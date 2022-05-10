package com.yunya365.mini.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Administrator
 */

public enum MemberStatusEnum {
    LOGOUT(0, "注销"),
    DISABLE(1, "禁用"),
    NORMAL(2, "正常）"),
    LOGGING_OUT(3, "注销中）"),
    ;
    private Integer code;
    private String value;

    MemberStatusEnum(Integer code, String value) {
        this.code = code;
        this.value = value;
    }

    /**
     * 根据value获取code
     * @param value value
     * @return value
     */
    public static Integer getCode(String value)
    {
        if(StringUtils.isNotBlank(value))
        {
            for(MemberStatusEnum memberStatusEnum : values())
            {
                if(StringUtils.equals(memberStatusEnum.getValue(), value))
                {
                    return memberStatusEnum.getCode();
                }
            }
        }
        return null;
    }

    public Integer getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }

    public boolean equals(String value) {
        return this.getValue().equals(value);
    }
}
