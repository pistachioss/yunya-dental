package com.yunya.framework.common.enums;

import java.util.Objects;

/**
 *  抗菌药物处方权
 * @author xiangyang
 * @date 2020/9/17
 */
public enum AntibiosisAuthorityEnum {
    /**
     * 非抗菌药物
     */
    ANTIBIOSIS_0("0", "非抗菌药物"),
    /**
     * 非限制级
     */
    ANTIBIOSIS_1("1", "非限制级"),
    /**
     * 限制级
     */
    ANTIBIOSIS_2("2", "限制级"),
    /**
     * 特殊级
     */
    ANTIBIOSIS_3("3", "特殊级"),

    ;

    private String code;
    private String value;

    AntibiosisAuthorityEnum(String code, String value) {
        this.code = code;
        this.value = value;
    }

    public String getCode() {
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
    public static String getValue(String code)
    {
        if(code != null)
        {
            for(AntibiosisAuthorityEnum useWayEnum : values())
            {
                if(Objects.equals(useWayEnum.getCode(), code))
                {
                    return useWayEnum.getValue();
                }
            }
        }
        return null;
    }

    public boolean equals(String code)
    {
        return this.code.equals(code);
    }
}
