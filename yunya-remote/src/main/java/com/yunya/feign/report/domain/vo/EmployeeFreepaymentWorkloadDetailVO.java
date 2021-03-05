package com.yunya.feign.report.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介: 员工免单支付工作量明细VO
 *
 * @author: chenl
 * @date: 2021/03/04
 * @description:
 * @since: 1.0.0
 */
@ApiModel("员工免单支付工作量明细VO")
@Data
@ToString
public class EmployeeFreepaymentWorkloadDetailVO implements Serializable {
  /** 账单ID */
  @ApiModelProperty("账单ID")
  private Integer billId;
  /** 收费时间 */
  @Excel(name = "收费日期", dateFormat = "yyyy-MM-dd HH:mm")
  @ApiModelProperty("收费时间")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  private String chargeDate;
  /** 账单编号 */
  @Excel(name = "账单编号")
  @ApiModelProperty("账单编号")
  private String billNum;
  /** 账单日期 */
  @Excel(name = "订单日期", dateFormat = "yyyy-MM-dd HH:mm")
  @ApiModelProperty("订单日期")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  private String orderDate;
  /** 账单日期 */
  @Excel(name = "账单日期", dateFormat = "yyyy-MM-dd HH:mm")
  @ApiModelProperty("账单日期")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  private String billDate;
  /** 患者姓名 */
  @Excel(name = "患者名称")
  @ApiModelProperty("患者姓名")
  private String patientName;
  /** 手机号 */
  @Excel(name = "手机号")
  @ApiModelProperty("手机号")
  private String mobile;
  /** 免单支付工作量 */
  @Excel(name = "免单支付工作量", scale = 2)
  @ApiModelProperty("免单支付工作量")
  private BigDecimal freePaymentWorkload;
}
