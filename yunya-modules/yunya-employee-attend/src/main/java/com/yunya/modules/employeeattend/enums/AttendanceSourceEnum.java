package com.yunya.modules.employeeattend.enums;

import java.util.Objects;

/**
 * 简介：考勤打卡来源枚举
 *
 * @author: chenlin
 * @Description:
 * @Date: 2020/11/12 16:15
 * @since: 1.0.0
 */
public enum AttendanceSourceEnum {

    EMPLOYEE_SCHEDULE((byte)0, "班次排班"),
    LEAVE((byte)1, "请假"),
    WORK_OVERTIME((byte)2, "加班"),
    FIELD((byte)3,"外勤");

    private final Byte code;
    private final String value;

    AttendanceSourceEnum(Byte code, String value) {
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
            for(AttendanceSourceEnum item : values())
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
