package com.yunya.feign.treatment.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介: 患者账单消费数据统计
 *
 * @author: chow
 * @date: 2020/11/16 20:33
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("患者账单消费数据统计")
public class PatientBillStatistics implements Serializable {
  /** 患者ID */
  @ApiModelProperty("患者ID")
  private Integer patientId;
  /** 账单原价合计 */
  @ApiModelProperty("账单原价合计")
  private BigDecimal billTotalOriginalPrice;
  /** 账单优惠合计 */
  @ApiModelProperty("账单优惠合计")
  private BigDecimal billTotalPrivilegePrice;
  /** 账单应收合计 */
  @ApiModelProperty("账单应收合计")
  private BigDecimal billTotalActualPrice;
  /** 账单欠费合计 */
  @ApiModelProperty("账单欠费合计")
  private BigDecimal billTotalArrears;
  /** 实收金额合计 */
  @ApiModelProperty("账单实收金额合计")
  private BigDecimal billTotalReceivedPrice;
  /** 免单支付合计 */
  @ApiModelProperty("免单支付合计")
  private BigDecimal freePaymentPrice;
}
