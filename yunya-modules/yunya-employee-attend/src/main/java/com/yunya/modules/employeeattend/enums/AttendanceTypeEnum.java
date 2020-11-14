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
public enum AttendanceTypeEnum {
    ONDUTY((byte)0, "上班"),
    OFFDUTY((byte)1, "下班");

    private final Byte code;
    private final String value;

    AttendanceTypeEnum(Byte code, String value) {
        this.code = code;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public Byte getCode() {
        return code;
    }

    public static String getValue(Integer code)
    {
        if(code != null)
        {
            for(AttendanceTypeEnum item : values())
            {
                if(Objects.equals(item.getCode(), code))
                {
                    return item.getValue();
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
