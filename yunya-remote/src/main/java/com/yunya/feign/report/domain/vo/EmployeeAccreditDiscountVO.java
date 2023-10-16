package com.yunya.feign.report.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import static com.yunya.framework.common.annation.Excel.Type.EXPORT;

/**
 * @author: chenlin
 * @date: 2023/10/16 13:36
 * @description: 员工授权折扣数据模型
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("员工授权折扣数据模型")
public class EmployeeAccreditDiscountVO implements Serializable {

    /** 订单id */
    @ApiModelProperty("订单id")
    private Integer billId;

    /** 订单明细id */
    @ApiModelProperty("订单明细id")
    private Integer billDetailId;

    /** 收费日期 */
    @Excel(name = "收费日期", dateFormat = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @ApiModelProperty("收费日期")
    private Date payeeDate;

    /** 门诊 */
    @Excel(name = "门诊")
    @ApiModelProperty("门诊")
    private String abbreviation;

    /** 挂号医生 */
    @Excel(name = "挂号医生")
    @ApiModelProperty("挂号医生")
    private String dentistName;

    /** 账单日期 */
    @Excel(name = "账单日期", dateFormat = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @ApiModelProperty("账单日期")
    private Date billDate;

    /** 患者 */
    @Excel(name = "患者")
    @ApiModelProperty("患者")
    private String patientName;

    /** 账单编号 */
    @Excel(name = "账单编号")
    @ApiModelProperty("账单编号")
    private String billNum;

    /** 授权折扣金额 */
    @Excel(name = "授权折扣金额", cellType = Excel.ColumnType.NUMERIC, isStatistics = true, type = EXPORT)
    @ApiModelProperty("授权折扣金额")
    private BigDecimal benefitAmount;

    /** 操作人 */
    @Excel(name = "操作人")
    @ApiModelProperty("操作人")
    private String operator;

    /** 授权折扣员工 */
    @Excel(name = "授权折扣员工")
    @ApiModelProperty("授权折扣员工")
    private String discountor;

    /** 备注 */
    @Excel(name = "备注")
    @ApiModelProperty("备注")
    private String remark;
}
