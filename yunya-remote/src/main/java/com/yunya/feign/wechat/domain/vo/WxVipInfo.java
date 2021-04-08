package com.yunya.feign.wechat.domain.vo;

import io.swagger.annotations.*;
import lombok.*;

import java.math.*;

/**
 * @description:
 * @author: xy
 * @date 2021/4/7 17:37
 **/
@Data
public class WxVipInfo {
    @ApiModelProperty(value = "头像")
    private String headImgUrl;
    @ApiModelProperty(value = "注册用户名")
    private String registerName;
    @ApiModelProperty(value = "注册手机号")
    private String registerMobile;
    @ApiModelProperty(value = "会员卡类型")
    private Integer memberType;
    @ApiModelProperty(value = "会员卡余额")
    private BigDecimal memberBalance;
}
