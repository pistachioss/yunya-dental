package com.yunya.feign.wechat.domain.model;


import lombok.Data;

@Data
public class WxAutoReplyNewsArticles {
    private String title;
    private String description;
    private String url;
    private String picurl;
}
