package com.yunya.feign.patient_central.domain.vo.web;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * 简介:
 *
 * @author: ylx
 * @date: 2022/5/20
 * @description:
 */
@Data
@ToString
@ApiModel("微信管理-地图范围内用户数量VO")
public class WxWechatMapBindNumFansVo {
    @ApiModelProperty("已绑定患者的微信用户数量")
    private Integer numBind;
    @ApiModelProperty("未绑定患者的微信用户数量")
    private Integer numNoBind;
}
