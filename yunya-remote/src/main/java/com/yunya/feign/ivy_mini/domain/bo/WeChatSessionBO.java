package com.yunya.feign.ivy_mini.domain.bo;

import lombok.Data;

/**
 * @description:
 * @author: xy
 * @date 2021/12/17 16:33
 **/
@Data
public class WeChatSessionBO {
    private String openId;
    private String unionId;
    private String sessionKey;

}
