package com.yunya365.mini.enums;

import com.yunya.framework.common.constant.PreFixCode;
import com.yunya.framework.common.model.RestError;

/**
 * @author xiangyang
 */

public enum IvyMiniError implements RestError {
    SUCCESS(0, "success"),
    JWT_NOT_LOGIN(1, "登录超时，请重新登录！"),
    JWT_ILLEGAL_ARGUMENT(2, "缺少token参数"),
    JWT_SIGNATURE(3, "不合法的token，请认真比对 token 的签名"),
    ACCOUNT_IS_LOGOUT(4, "用户已登出"),
    WX_SERVER_ERROR(5, "微信服务器异常"),
    MEMBER_NOT_EXIST(6, "用户不存在，请先注册"),

    ;
    private final Integer code;
    private final String value;

    IvyMiniError(Integer code, String value) {
        this.code = code;
        this.value = value;
    }
    @Override
    public int getCode() {
        if (code.equals(0)) {
            return code;
        }
        return PreFixCode.IVY_MINI.getCode() * 1000 + code;
    }

    @Override
    public String getMessage() {
        return value;
    }
}
