package com.yunya.framework.common.enums;

import java.util.Objects;

/**
 *  职称
 * @author xiangyang
 * @date 2020/9/17
 */
public enum TechnicalTitleEnum {
    /**
     * 主任医师
     */
    TITLE_01("01", "主任医师"),
    /**
     * 副主任医师
     */
    TITLE_02("02", "副主任医师"),
    /**
     * 主治医师
     */
    TITLE_03("03", "主治医师"),
    /**
     * 医师
     */
    TITLE_04("04", "医师"),
    /**
     * 助理医师
     */
    TITLE_05("05", "助理医师"),
    /**
     * 理疗师
     */
    TITLE_10("10", "理疗师"),
    ;

    private String code;
    private String value;

    TechnicalTitleEnum(String code, String value) {
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
            for(TechnicalTitleEnum useWayEnum : values())
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
