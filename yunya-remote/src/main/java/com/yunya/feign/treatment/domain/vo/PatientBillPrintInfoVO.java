package com.yunya.feign.treatment.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
  @ApiModelProperty("账单ID")
  private Integer billRecordId;
  /** 账单日期 */
  @ApiModelProperty("账单日期")
  private String billDate;
  /** 诊所ID */
  @ApiModelProperty("诊所ID")
  private Integer clinicId;
  /** 开单门诊 */
  @ApiModelProperty("开单门诊")
  private String clinicName;
  /** 医生ID */
  @ApiModelProperty("医生ID")
  private Integer dentistId;
  /** 主治医生 */
  @ApiModelProperty("主治医生")
  private String dentistName;
  /** 应收合计 */
  @ApiModelProperty("应收合计")
  private BigDecimal totalActualAmount;
  /** 免单合计 */
  @ApiModelProperty("免单合计")
  private BigDecimal totalFreePayAmount;
  /** 实收金额 */
  @ApiModelProperty("实收金额")
  private BigDecimal totalReceivedAmount;
  /** 欠费金额 */
  @ApiModelProperty("欠费金额")
  private BigDecimal totalDebtAmount;
  /** 账单明细 */
  private List<BillDetailChargeVO> billDetails;
}
