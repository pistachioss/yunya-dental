package com.yunya.modules.discount.enums;

import java.util.*;

/**
 * @author xiangyang
 * @date 2020/9/10
 */
public enum RangTypeEnum {
    SELECT_ALL(0, "全选"),
    SELECT_ITEM_CATEGORY(1, "选项目分类"),
    SELECT_ITEM_DETAIL(2, "选项目明细"),
    ;

    private Integer code;
    private String value;

    RangTypeEnum(Integer code, String value) {
        this.code = code;
        this.value = value;
    }

    public Integer getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }

    public static String getValue(Integer code) {
        if (code != null) {
            for (RangTypeEnum rangTypeEnum : values()) {
                if (Objects.equals(rangTypeEnum.getCode(), code)) {
                    return rangTypeEnum.getValue();
                }
            }
        }
        return null;
    }

    public boolean equals(Integer code) {
        return this.code.equals(code);
    }
}
