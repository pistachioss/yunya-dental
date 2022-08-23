package com.yunya.feign.wechat.domain.model;

import lombok.Data;

@Data
public class WxAutoNewsReplyModel {
    /**
     * 接收者openid.
     */
    private String touser;
    private String msgtype;
    private WxAutoReplyNews news;
}
