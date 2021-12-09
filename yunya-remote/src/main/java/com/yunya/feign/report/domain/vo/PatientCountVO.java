package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介：患者数量VO
 *
 * @author: chenlin
 * @Description: 患者数量VO
 * @Date: 2021/12/7 16:04
 * @since: 1.0.0
 */
@Data
@ApiModel("患者数量VO")
@ToString
public class PatientCountVO extends PatientVO implements Serializable {

    @ApiModelProperty("数量")
    private Integer count;
}
