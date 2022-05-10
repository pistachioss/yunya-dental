package com.yunya.feign.ivy_mini.domain.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * @author xiangyang
 */

public enum LoginEnum {
    /**
     * 小程序
     */
    MINI(0, "mini "),
    /**
     * 小程序授权
     */
    MINI_AUTH(1, "mini auth "),
    /**
     * web登录
     */
    WEB(2, "web "),
    ;

    private final Integer code;
    private final String value;

    LoginEnum(Integer code, String value) {
        this.code = code;
        this.value = value;
    }

    /**
     * 解析类型
     *
     * @param value value
     * @return LoginEnum
     */
    public static LoginEnum resolveEvent(String value) {
        for (LoginEnum loginEnum : LoginEnum.values()) {
            if (StringUtils.equals(value, loginEnum.getValue())) {
                return loginEnum;
            }
        }
        return null;
    }

    public Integer getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }

}
