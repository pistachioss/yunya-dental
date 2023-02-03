package com.yunya.framework.common.enums;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 患者预付款账号分类枚举
 *
 * @author: chenlin
 * @date: 2023/2/3 19:48
 * @description:
 * @since: 1.0.0
 */
public enum PatientPrepaymentTypeEnum {

    NORMAL(false, 1, "预付款", "Y"),
    ORTHADANTIC(true, 2, "正畸预付款", "ZY"),
    WHITENING(true, 3, "美白预付款", "MY"),
    IMPLANT(true,4, "种植预付款", "ZZY"),
    ;

    /** 是否专项 */
    private Boolean isSpecial;

    /** 分类类型 */
    private Integer type;

    /** 分类名称 */
    private String name;

    /** 生成编号时使用的前缀 */
    private String prefix;

    PatientPrepaymentTypeEnum(Boolean isSpecial, Integer type, String name, String prefix) {
        this.isSpecial = isSpecial;
        this.type = type;
        this.name = name;
        this.prefix = prefix;
    }

    public static List<PatientPrepaymentTypeEnum> values(boolean isSpecial) {
        return Stream.of(values()).filter(type->type.isSpecial).collect(Collectors.toList());
    }

    public String getName() {
        return name;
    }

    public Integer getType() {
        return type;
    }

    public String getPrefix() {
        return prefix;
    }

    public static PatientPrepaymentTypeEnum getTypeEnum(Integer type) {
        if (type != null) {
            for (PatientPrepaymentTypeEnum item : values()) {
                if (Objects.equals(item.getType(), type)) {
                    return item;
                }
            }
        }
        return null;
    }

    public boolean equals(Integer type)
    {
        return this.type.equals(type);
    }
}
