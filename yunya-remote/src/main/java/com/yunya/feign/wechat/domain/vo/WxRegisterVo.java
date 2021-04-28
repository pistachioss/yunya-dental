package com.yunya.feign.wechat.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: xy
 * @date 2021/4/16 19:06
 **/
@Data
@ApiModel(value = "微信用户注册返回")
public class WxRegisterVo {
    @ApiModelProperty(value = "患者id")
    private Integer patientId;
    @ApiModelProperty(value = "患者名")
    private String patientName;
    @ApiModelProperty(value = "手机号")
    private String mobile;
}
