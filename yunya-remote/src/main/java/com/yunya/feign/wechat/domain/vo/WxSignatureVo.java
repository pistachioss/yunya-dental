package com.yunya.feign.wechat.domain.vo;

import lombok.Data;

/**
 * @description:
 * @author: xy
 * @date 2021/3/26 9:38
 **/
@Data
public class WxSignatureVo {
    private String appId;
    private String nonceStr;
    private String signature;
    private String timeStamp;
}
