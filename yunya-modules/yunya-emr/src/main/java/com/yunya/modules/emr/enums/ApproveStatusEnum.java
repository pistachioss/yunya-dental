package com.yunya.modules.emr.enums;

import java.util.*;

/**
 * @author xiangyang
 */

public enum ApproveStatusEnum {
    APPROVE_PENDING(0, "0：待审批"),
    AUDIT_PASS(1, "同意"),
    AUDIT_REJECT(2, "拒绝"),
    ;
    private Integer code;
    private String value;

    ApproveStatusEnum(Integer code, String value) {
        this.code = code;
        this.value = value;
    }

    public Integer getCode()
    {
        return code;
    }

    public String getValue()
    {
        return value;
    }

    public static String getValue(Integer code)
    {
        if(code != null)
        {
            for(ApproveStatusEnum statusEnum : values())
            {
                if(Objects.equals(statusEnum.getCode(), code))
                {
                    return statusEnum.getValue();
                }
            }
        }
        return null;
    }
}
