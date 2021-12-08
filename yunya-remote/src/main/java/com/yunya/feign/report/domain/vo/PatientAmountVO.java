package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介：患者金额VO
 *
 * @author: chenlin
 * @Description: 患者金额VO
 * @Date: 2021/12/7 16:27
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("患者金额VO")
public class PatientAmountVO extends PatientVO implements Serializable {

    @ApiModelProperty("金额")
    private BigDecimal amount;
}
