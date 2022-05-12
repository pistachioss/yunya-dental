package com.yunya.feign.wechat.domain.model;

import lombok.Builder;
import lombok.Data;

@Data
public class WxAutoReplyModel {
    /**
     * 接收者openid.
     */
    private String touser;
    private String msgtype;
    private WxAutoReplyText text;
}
