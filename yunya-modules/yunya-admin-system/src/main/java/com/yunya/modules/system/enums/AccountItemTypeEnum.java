package com.yunya.modules.system.enums;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @description: 入账方式类型枚举
 * @author: chenlin
 * @date: 2023-02-05 10:53
 */
public enum AccountItemTypeEnum {

    CASH("现金", 0),
    PRE_SOLE("预售", 1),
    DISCOUNT("优惠", 2),
    PLATFORM_SETTLEMENT("平台结算", 3),
    ;

    private final String value;
    private final Integer type;

    AccountItemTypeEnum(String value, Integer code) {
        this.value = value;
        this.type = code;
    }

    public String getValue() {
        return value;
    }

    public Integer getType() {
        return type;
    }

    public static String getValue(Integer type) {
        if (type != null) {
            for (AccountItemTypeEnum item : values()) {
                if (Objects.equals(item.getType(), type)) {
                    return item.getValue();
                }
            }
        }
        return null;
    }

    /**
     * 不含预售类型列表
     *
     * @return
     */
    public static List<Integer> unPreSoldTypes() {
        return Stream.of(values())
                .filter(item->!PRE_SOLE.equals(item.type))
                .map(AccountItemTypeEnum::getType)
                .collect(Collectors.toList());
    }

    public boolean equals(Integer type)
    {
        return this.type.equals(type);
    }
}
