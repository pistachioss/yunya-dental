package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介：员工项目的工作量统计VO
 *
 * @author: chenlin
 * @Description: 员工项目的工作量统计VO
 * @Date: 2021/10/26 12:13
 * @since: 1.0.0
 */
@Data
@ApiModel("员工项目的工作量统计VO")
@ToString
@EqualsAndHashCode(callSuper = true)
public class EmployeeTariffWorkloadVO extends ClinicEmployeTariffInfoVO implements Serializable {
    @ApiModelProperty("数量")
    private Integer quantity;

    @ApiModelProperty("工作量")
    private BigDecimal workload;

    @ApiModelProperty("免单工作量")
    private BigDecimal freeWorkload;

    @ApiModelProperty("账单id")
    private Integer billId;

    @ApiModelProperty("账单编号")
    private String orderNum;

    @ApiModelProperty("患者姓名")
    private String name;

    @ApiModelProperty("患者电话")
    private String mobile;

    @ApiModelProperty("开单备注")
    private String remark;

    @ApiModelProperty("挂号医生")
    private String employeeName;

    @ApiModelProperty("账单日期")
    private String billDate;

    @ApiModelProperty("收费数量")
    private Integer billQuantity;
}