package com.yunya.feign.treatment.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author: chenlin
 * @date: 2023/8/4 13:39
 * @description: 会员授权码请求模型
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("会员授权码请求模型")
public class MemberAuthorizedCodeForm implements Serializable {

    /** 患者id */
    @ApiModelProperty(value = "患者id", required = true)
    @NotNull(message = "患者id不能为空")
    private Integer patientId;

    /** 患者姓名 */
    @ApiModelProperty("患者姓名")
    private String patientName;
    
    /** 患者手机号 */
    @ApiModelProperty("患者手机号")
    private String mobile;

    /** 会员卡类型id */
    @ApiModelProperty("会员卡类型id")
    private Integer memberTypeId;
}
