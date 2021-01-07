package com.yunya.feign.report.domain.vo;

import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介: 当月收欠费（使用优惠）账单信息VO
 *
 * @author: chow
 * @date: 2021/1/7 11:09
 * @description:
 * @since: 1.0.0
 */
@ApiModel("当月收欠费（使用优惠）账单信息VO")
@Data
@ToString
public class CurrentMonthBillCollectionDebtVO implements Serializable {
  /** 账单ID */
  @ApiModelProperty("账单ID")
  private Integer billId;
  /** 收欠费日期（使用优惠） */
  @Excel(name = "收欠费日期（使用优惠）")
  @ApiModelProperty("收欠费日期（使用优惠）")
  private String collectDebtDate;
  /** 账单日期 */
  @Excel(name = "账单日期")
  @ApiModelProperty("账单日期")
  private String billDate;
  /** 账单编号 */
  @Excel(name = "账单编号")
  @ApiModelProperty("账单编号")
  private String billNum;
  /** 当月/非当月账单 */
  @Excel(name = "当月/非当月账单")
  @ApiModelProperty("当月/非当月账单")
  private String currentMonthBill;
  /** 患者ID */
  @ApiModelProperty("患者ID")
  private Integer patientId;
  /** 患者姓名 */
  @Excel(name = "患者姓名")
  @ApiModelProperty("患者姓名")
  private String patientName;
  /** 患者手机号 */
  @Excel(name = "手机号")
  @ApiModelProperty("患者手机号")
  private String patientMobile;
  /** 挂号医生ID */
  @ApiModelProperty("挂号医生ID")
  private Integer regDentistId;
  /** 挂号医生姓名 */
  @Excel(name = "挂号医生")
  @ApiModelProperty("挂号医生姓名")
  private String regDentistName;
  /** 收费人ID */
  @ApiModelProperty("收费人ID")
  private Integer payeeId;
  /** 收费人姓名 */
  @Excel(name = "收费人")
  @ApiModelProperty("收费人姓名")
  private String payeeName;
}
