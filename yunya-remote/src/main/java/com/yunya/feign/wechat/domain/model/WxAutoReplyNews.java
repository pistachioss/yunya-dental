package com.yunya.feign.wechat.domain.model;


import lombok.Data;

import java.util.List;

@Data
public class WxAutoReplyNews {
    private List<WxAutoReplyNewsArticles> articles;
}
