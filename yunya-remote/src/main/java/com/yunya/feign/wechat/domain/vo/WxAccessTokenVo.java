package com.yunya.feign.wechat.domain.vo;

import lombok.Data;

/**
 * @description:
 * @author: xy
 * @date 2021/3/24 17:29
 **/
@Data
public class WxAccessTokenVo {
    private String access_token;
    private Long expires_in;
    private Integer errcode;
    private String errmsg;
}
