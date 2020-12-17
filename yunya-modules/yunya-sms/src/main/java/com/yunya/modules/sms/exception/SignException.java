package com.yunya.modules.sms.exception;

import com.yunya.framework.common.exception.BaseException;

/**
 * 简介：
 *
 * @author: chenlin
 * @Description:
 * @Date: 2020/12/14 11:19
 * @since: 1.0.0
 */
public class SignException extends BaseException {

    public SignException(String msg, int exceptionCode) {
        super(msg, exceptionCode);
    }

    public SignException(String msg) {
        this(msg, null);
    }

    public SignException(String msg, Exception e) {
        super(msg, e);
    }
}
