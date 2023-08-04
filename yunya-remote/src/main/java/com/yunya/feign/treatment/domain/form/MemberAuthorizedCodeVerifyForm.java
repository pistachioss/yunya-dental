package com.yunya.feign.treatment.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author: chenlin
 * @date: 2023/8/4 14:50
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("会员授权码验证模型")
public class MemberAuthorizedCodeVerifyForm implements Serializable {

    /** 患者id */
    @ApiModelProperty(value = "患者id", required = true)
    @NotNull(message = "患者id不能为空")
    private Integer patientId;

    /** 验证码 */
    @ApiModelProperty(value = "验证码", required = true)
    @NotEmpty(message = "验证码不能为空")
    private String code;
}
