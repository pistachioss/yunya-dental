package com.yunya.framework.common.model;


import com.yunya.framework.common.constant.CommonConstants;

/**
 *
 * @author ace
 * @date 2017/8/25
 */
public class TokenForbiddenResponse extends BaseResponse {
    public TokenForbiddenResponse(String message) {
        super(CommonConstants.TOKEN_FORBIDDEN_CODE, message);
    }
}
