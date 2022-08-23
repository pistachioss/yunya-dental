package com.yunya.feign.wechat.domain.model;

import lombok.Data;

@Data
public class WxAutoImageReplyModel {
    /**
     * 接收者openid.
     */
    private String touser;
    private String msgtype;
    private WxAutoReplyImage image;
}
