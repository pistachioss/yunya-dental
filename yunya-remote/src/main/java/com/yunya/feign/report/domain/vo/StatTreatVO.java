package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * 简介：
 *
 * @author: chenlin
 * @Description:
 * @Date: 2021/12/30 15:45
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("就诊统计VO")
public class StatTreatVO {
    @ApiModelProperty("院区ID")
    private Integer campusId;
    @ApiModelProperty("门诊ID")
    private Integer orgId;
    @ApiModelProperty("患者ID")
    private Integer patientId;
    @ApiModelProperty("医生ID")
    private Integer dentistId;
    @ApiModelProperty("就诊类型：0-初诊，1-复诊")
    private Byte treatType;
    @ApiModelProperty("就诊ID")
    private Integer treatmentId;
    @ApiModelProperty("就诊日期")
    private Integer treatDate;
}
