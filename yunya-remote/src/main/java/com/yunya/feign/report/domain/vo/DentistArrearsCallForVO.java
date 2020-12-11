package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介: 医生所属欠费账单信息VO
 *
 * @author: chow
 * @date: 2020/12/10 12:55
 * @description:
 * @since: 1.0.0
 */
@ApiModel("医生所属欠费账单信息VO")
@Data
@ToString
public class DentistArrearsCallForVO implements Serializable {
  /** 医生ID */
  @ApiModelProperty("医生ID")
  private Integer dentistId;
  /** 医生姓名 */
  @ApiModelProperty("医生姓名")
  private String dentistName;
  /** 欠费账单数量 */
  @ApiModelProperty("欠费账单数量")
  private Integer outstandingBillCount;
  /** 欠费总额 */
  @ApiModelProperty("欠费总额")
  private BigDecimal totalArrearsAmount;
}
