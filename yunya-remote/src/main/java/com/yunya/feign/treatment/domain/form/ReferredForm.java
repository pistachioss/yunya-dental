package com.yunya.feign.treatment.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;

/**
 * 简介: 转诊
 * <$>
 *
 * @author: 杨柳絮
 * @date: $ $
 * @description:
 * @since: 1.0.0
 * @param: $
 * @return: $
 */
@ApiModel("转诊参数模型")
@Data
@ToString
public class ReferredForm {

    /** 患者id */
    @ApiModelProperty(value = "患者id", required = true)
    @NotNull(message = "患者id不能为空！")
    private Integer patientId;

    /** 挂号id */
    @ApiModelProperty(value = "挂号id", required = true)
    @NotNull(message = "挂号id不能为空！")
    private Integer registeredId;

    /** 门诊id */
    @ApiModelProperty(value = "门诊id", required = true)
    @NotNull(message = "门诊id不能为空！")
    private Integer orgId;

    /** 转诊医生id */
    @ApiModelProperty(value = "转诊医生id", required = true)
    @NotNull(message = "转诊医生id不能为空！")
    private Integer userId;

    /** 转诊医生的科室ID */
    @ApiModelProperty("科室ID")
    private Integer depId;

    /** 被转诊医生id */
    @ApiModelProperty(value = "被转诊医生id", required = true)
    @NotNull(message = "被转诊医生id不能为空！")
    private Integer referredId;

    /** 被转诊医生的科室ID */
    @ApiModelProperty("被转诊医生的科室ID")
    private Integer referredDepId;

    /** 挂号备注 */
    @ApiModelProperty("转诊备注")
    private String remark;

}
