package com.yunya.modules.emr.enums;

import java.util.Objects;

/**
 * @author xiangyang
 */

public enum TemplateTypeEnum {
    FIRST_VISIT(0, "初诊"),
    FOLLOW_UP(1, "复诊"),
    ;
    private final Integer code;
    private final String value;

    TemplateTypeEnum(Integer code, String value) {
        this.code = code;
        this.value = value;
    }

    public Integer getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }

    public static String getValue(Integer code)
    {
        if(code != null)
        {
            for(TemplateTypeEnum enable : values())
            {
                if(Objects.equals(enable.getCode(), code))
                {
                    return enable.getValue();
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
