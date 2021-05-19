package com.yunya.feign.treatment.domain.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 简介: 患者账单打印信息VO
 *
 * @author: chow
 * @date: 2021/5/19 17:40
 * @description:
 * @since: 1.0.0
 */
@ApiModel("患者账单打印信息VO")
@Data
@ToString
public class PatientBillPrintInfoVO implements Serializable {
  /** 账单ID */
  private Integer billRecordId;
  /** 账单日期 */
  private String billDate;
  /** 开单门诊 */
  private String clinicName;
  /** 主治医生 */
  private String dentistName;
  /** 账单明细 */
  private List<BillDetailChargeVO> billDetailCharges;
  /** 应收合计 */
  private BigDecimal totalActualAmount;
  /** 免单金额 */
  private BigDecimal totalFreePayAmount;
  /** 实收金额 */
  private BigDecimal totalReceivedAmount;
  /** 欠费金额 */
  private BigDecimal totalDebtAmount;
}
