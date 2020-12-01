package com.yunya.modules.employeeattend.enums;

import java.util.Objects;

/**
 * 简介：考勤打卡状态枚举
 *
 * @author: chenlin
 * @Description:
 * @Date: 2020/11/12 16:15
 * @since: 1.0.0
 */
public enum AttendanceStatusEnum {
    ONDUTY_PUNCH((byte)0, "正常上班"),
    LATER_PUNCH((byte)1, "迟到"),
    OFFDUTY_PUNCH((byte)2, "正常下班"),
    EARLY_PUNCH((byte)3,"早退"),
    UNVALID_PUNCH((byte)4,"无效卡");

    private final Byte code;
    private final String value;

    AttendanceStatusEnum(Byte code, String value) {
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
            for(AttendanceStatusEnum item : values())
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
