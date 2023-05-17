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
@ApiModel("患者是否有某些收费项目VO")
public class PatientHasBillItemVO implements Serializable {

    @ApiModelProperty("患者ID")
    private Integer patientId;

    @ApiModelProperty("是否买过电动牙刷")
    private Integer had1;

    @ApiModelProperty("是否待洁牙")
    private Integer had2;
}
