package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介：患者日期VO
 *
 * @author: chenlin
 * @Description: 患者日期VO
 * @Date: 2021/12/7 16:05
 * @since: 1.0.0
 */
@Data
@ApiModel("患者日期VO")
@ToString
public class PatientDateVO extends PatientVO implements Serializable {

    @ApiModelProperty("日期")
    private String date;
}
