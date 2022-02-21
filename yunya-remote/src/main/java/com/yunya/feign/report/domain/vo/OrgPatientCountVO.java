package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介：门诊患者数量VO
 *
 * @author: chenlin
 * @Description: 门诊患者数量VO
 * @Date: 2022/2/15 12:33
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("门诊患者数量VO")
public class OrgPatientCountVO extends PatientCountVO implements Serializable {
    /** 门诊id*/
    @ApiModelProperty("门诊id")
    private Integer orgId;
}
