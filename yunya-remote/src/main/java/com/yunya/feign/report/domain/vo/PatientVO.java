package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介：患者信息VO
 *
 * @author: chenlin
 * @Description: 患者信息VO
 * @Date: 2021/12/7 16:03
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("患者信息VO")
public class PatientVO implements Serializable {
    @ApiModelProperty("门诊id")
    private Integer orgId;

    @ApiModelProperty("患者ID")
    private Integer patientId;
}
