package com.yunya365.wechat.enums;

import com.yunya.framework.common.constant.PreFixCode;
import com.yunya.framework.common.model.RestError;

/**
 * @author xiangyang
 */

public enum WeChatError implements RestError {
    USER_NOT_FOLLOW(1, "非关注公众号用户请先关注艾维口腔公众号！"),
    DICT_NO_CONFIG(2, "亲属关系不存在，请先配置"),
    ;
    private Integer code;
    private String value;

    WeChatError(Integer code, String value) {
        this.code = code;
        this.value = value;
    }
    @Override
    public Integer getCode() {
        if (code.equals(0)) {
            return code;
        }
        return PreFixCode.WCHAR.getCode() * 1000 + code;
    }

    @Override
    public String getMessage() {
        return value;
    }
}
