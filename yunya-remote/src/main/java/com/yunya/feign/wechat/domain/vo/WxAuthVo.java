package com.yunya.feign.wechat.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: xy
 * @date 2021/4/2 17:18
 **/
@ApiModel(value = "微信公众号授权返回")
@Data
public class WxAuthVo {
    @ApiModelProperty(value = "是否注册过")
    private Boolean isRegister;
    @ApiModelProperty(value = "用户openid")
    private String openId;
}
