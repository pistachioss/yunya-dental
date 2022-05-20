package com.yunya.feign.patient_central.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.aspectj.lang.annotation.DeclareAnnotation;

/**
 * 简介:
 *
 * @author: ylx
 * @date: 2022/5/20
 * @description:
 */
@Data
@ApiModel(value = "WxFansWechatQueryForm",description = "微信用户-启用禁用")
public class WxFansWechatUpdateForm {

    @ApiModelProperty("id")
    private Integer id;

    /**
     * 用户状态（0-注销 1-禁用 2-正常 3-注销中）
     */
    @ApiModelProperty("用户状态（0-注销 1-禁用 2-正常 3-注销中）")
    private Integer fansStatus;

}
