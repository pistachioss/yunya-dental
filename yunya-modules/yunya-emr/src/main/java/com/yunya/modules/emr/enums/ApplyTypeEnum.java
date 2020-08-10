package com.yunya.modules.emr.enums;

import java.util.*;

/**
 * @author xiangyang
 */

public enum ApplyTypeEnum {
    ADD(0, "新增"),
    UPDATE(1, "修改"),
    ;

    private final Integer code;
    private final String value;

    ApplyTypeEnum(Integer code, String value) {
        this.code = code;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public Integer getCode() {
        return code;
    }

    public static String getValue(Integer code)
    {
        if(code != null)
        {
            for(ApplyTypeEnum applyEnum : values())
            {
                if(Objects.equals(applyEnum.getCode(), code))
                {
                    return applyEnum.getValue();
                }
            }
        }
        return null;
    }
}
