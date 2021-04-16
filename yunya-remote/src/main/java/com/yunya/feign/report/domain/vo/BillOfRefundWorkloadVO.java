package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 简介：退费工作量
 *
 * @author: chenlin
 * @Description: 退费工作量
 * @Date: 2021/3/29 17:22
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("退费工作量")
public class BillOfRefundWorkloadVO implements Serializable {

    /** 门诊ID*/
    @ApiModelProperty("门诊ID")
    private Integer orgId;

    /** 退费日期*/
    @ApiModelProperty("退费日期")
    private Date refundDate;

    /** 退费时的总工作量*/
    @ApiModelProperty("退费时的总工作量")
    private BigDecimal totalRefundWorkload;

    /** 月份*/
    private String month;
}
