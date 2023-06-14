package com.yunya.feign.patient_central.domain.vo.web;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 简介:
 *
 * @author: ylx
 * @date: 2023/5/12
 * @description:
 */
@Data
public class WxFansWithMobile {
    @ApiModelProperty("手机号")
    private String mobile;
}
