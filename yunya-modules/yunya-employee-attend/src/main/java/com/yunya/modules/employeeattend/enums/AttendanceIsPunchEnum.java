package com.yunya.modules.employeeattend.enums;

import java.util.Objects;

/**
 * 简介：考勤打卡类型枚举
 *
 * @author: chenlin
 * @Description:
 * @Date: 2020/11/12 16:15
 * @since: 1.0.0
 */
public enum AttendanceIsPunchEnum {
    UNPUNCH((byte)0, "未打卡"),
    PUNCHED((byte)1, "已打卡");

    private final Byte code;
    private final String value;

    AttendanceIsPunchEnum(Byte code, String value) {
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
            for(AttendanceIsPunchEnum item : values())
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
