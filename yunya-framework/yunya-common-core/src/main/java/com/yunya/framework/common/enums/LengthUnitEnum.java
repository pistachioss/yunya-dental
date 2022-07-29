package com.yunya.framework.common.enums;

import java.util.Objects;

/**
 * 长度单位枚举
 */
public enum LengthUnitEnum {
    /** 厘米*/
    CENTIMETER("厘米",1),
    /** 分米*/
    DECIMETER("分米",2),
    /** 米*/
    METER("米", 3),
    /** 公里or千米*/
    KILOMETER("公里or千米", 4);

    private final String value;
    private final Integer code;

    LengthUnitEnum(String value, Integer code) {
      this.value = value;
      this.code = code;
    }

    public String getValue() {
      return value;
    }

    public Integer getCode() {
      return code;
    }

    public static String getValue(Integer code) {
      if (code != null) {
        for (LengthUnitEnum item : values()) {
          if (Objects.equals(item.getCode(), code)) {
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