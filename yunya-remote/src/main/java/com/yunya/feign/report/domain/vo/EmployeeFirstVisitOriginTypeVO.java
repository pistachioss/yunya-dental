package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介：初诊医生的患者来源VO
 *
 * @author: chenlin
 * @Description: 初诊医生的患者来源VO
 * @Date: 2021/12/8 17:14
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("初诊医生的患者来源VO")
public class EmployeeFirstVisitOriginTypeVO implements Serializable {
    @ApiModelProperty("门诊ID")
    private Integer orgId;

    private Integer employeeId;

    private Integer originTypeId;

    private String originTypeName;

    private Integer count;
}

