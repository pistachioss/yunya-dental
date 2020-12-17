package com.yunya.modules.sms.enums;

import java.util.Objects;

/**
 * 简介：阿里云审批状态
 *
 * @author: chenlin
 * @Description:
 * @Date: 2020/11/12 16:15
 * @since: 1.0.0
 */
public enum SmsApprovalStatusEnum {
    APPROVALING((byte)0, "审核中"),
    APPROVAL_PASS((byte)1, "审核通过"),
    APPROVAL_FAIL((byte)2, "审核失败");

    private final Byte code;
    private final String value;

    SmsApprovalStatusEnum(Byte code, String value) {
        this.code = code;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public Byte getCode() {
        return code;
    }

    public static String getValue(Byte code)
    {
        if(code != null)
        {
            for(SmsApprovalStatusEnum item : values())
            {
                if(Objects.equals(item.getCode(), code))
                {
                    return item.getValue();
                }
            }
        }
        return null;
    }

    public boolean equals(Byte code)
    {
        return this.code.equals(code);
    }
}
