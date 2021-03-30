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

/**
 * 简介: 折扣&免单支付VO
 *
 * @author: chow
 * @date: 2020/12/7 10:40
 * @description:
 * @since: 1.0.0
 */
@ApiModel("折扣&免单支付VO")
@Data
@ToString
public class BillDiscountAndFreePaymentVO implements Serializable {

  /** 收费日期 */
  @Excel(name = "收费日期", dateFormat = "yyyy-MM-dd")
  @ApiModelProperty("收费日期")
  @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
  private Date payeeDate;

  /** 门诊*/
  @Excel(name = "门诊")
  @ApiModelProperty("门诊")
  private String abbreviation;

  @Excel(name = "挂号医生")
  @ApiModelProperty("挂号医生")
  private String employeeName;

  /** 账单日期 */
  @Excel(name = "账单日期", dateFormat = "yyyy-MM-dd")
  @ApiModelProperty("账单日期")
  @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
  private Date billDate;

  /** 患者 */
  @Excel(name = "患者")
  @ApiModelProperty("患者")
  private String patientName;

  /** 账单编号 */
  @Excel(name = "账单编号")
  @ApiModelProperty("账单编号")
  private String billNum;

  /** 折扣免单 */
  @Excel(name = "折扣免单")
  @ApiModelProperty("折扣免单")
  private String discountFree;

  /** 金额 */
  @Excel(name = "金额")
  @ApiModelProperty("金额")
  private BigDecimal amount;

  /** 操作人 */
  @Excel(name = "操作人")
  @ApiModelProperty("操作人")
  private String payeeUser;

  /** 授权人 */
  @Excel(name = "授权人")
  @ApiModelProperty("授权人")
  private String discountUser;

  /** 备注 */
  @Excel(name = "备注")
  @ApiModelProperty("备注")
  private String remark;
}
