package com.yunya.feign.ivy_mini.domain.vo;

import lombok.Data;

/**
 * @description:
 * @author: xy
 * @date 2021/3/24 17:29
 **/
@Data
public class WxAccessTokenVo {
    private String accessToken;
    private Long expiresIn;
    private Integer errcode;
    private String errmsg;
}
