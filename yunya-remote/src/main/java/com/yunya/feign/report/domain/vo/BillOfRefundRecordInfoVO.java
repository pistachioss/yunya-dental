package com.yunya.feign.report.domain.vo;

import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介: 账单退费记录VO模型
 *
 * @author: chow
 * @date: 2020/11/23 13:31
 * @description:
 * @since: 1.0.0
 */
@Data
@ApiModel("账单退费记录VO模型")
public class BillOfRefundRecordInfoVO implements Serializable {
  /** 账单退费记录ID */
  @ApiModelProperty("账单退费记录ID")
  private Integer billRefundRecordId;
  /** 退费日期 */
  @Excel(name = "退费日期")
  @ApiModelProperty("退费日期")
  private String refundDate;
  /** 开单日期 */
  @ApiModelProperty("订单日期")
  private String orderDate;
  /** 账单日期 */
  @Excel(name = "账单日期")
  @ApiModelProperty("账单日期")
  private String billDate;
  /** 账单编号 */
  @Excel(name = "账单编号")
  @ApiModelProperty("账单编号")
  private String billNum;
  /** 患者ID */
  @ApiModelProperty("患者ID")
  private Integer patientId;
  /** 患者姓名 */
  @Excel(name = "患者姓名")
  @ApiModelProperty("患者姓名")
  private String patientName;
  /** 患者性别 */
  @ApiModelProperty("性别（0-男；1-女)")
  private Byte gender;
  /** 患者手机号 */
  @Excel(name = "患者手机号")
  @ApiModelProperty("患者手机号")
  private String mobile;
  /** 挂号医生ID */
  @ApiModelProperty("挂号医生ID")
  private Integer regDentistId;
  /** 挂号医生姓名 */
  @Excel(name = "挂号医生")
  @ApiModelProperty("挂号医生姓名")
  private String regDentistName;
  /** 实收金额 */
  @Excel(name = "实收金额")
  @ApiModelProperty("实收金额")
  private BigDecimal actualAmount;
  /** 已收金额 */
  @Excel(name = "已收金额")
  @ApiModelProperty("已收金额")
  private BigDecimal receivedAmount;
  /** 本次退费金额 */
  @Excel(name = "本次退费金额")
  @ApiModelProperty("本次退费金额")
  private BigDecimal refundAmount;
  /** 退费人ID */
  @ApiModelProperty("退费人ID")
  private Integer refundOperatorId;
  /** 退费人ID */
  @Excel(name = "退费人")
  @ApiModelProperty("退费人姓名")
  private String refundOperatorName;
  /** 退费原因 */
  @Excel(name = "退费原因")
  @ApiModelProperty("退费原因")
  private String refundReason;
  /** 就诊ID */
  @ApiModelProperty("就诊ID")
  private Integer treatmentRecordId;
}
