package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

@ApiModel("门诊划扣数据统计VO")
@Data
@ToString
public class DeductionDataStatisticVO implements Serializable {
  /** 会员卡充值总额（本金+赠金） */
  @ApiModelProperty("划扣卡售卖金额合计")
  private BigDecimal totalDeductionRechargeAmount;
  /** 会员卡消费总额（本金+赠金） */
  @ApiModelProperty("划扣卡消耗金额合计")
  private BigDecimal totalDeductionExpendAmount;
  /** 会员卡退费总额（本金+赠金） */
  @ApiModelProperty("划扣卡退费金额合计")
  private BigDecimal totalDeductionRefundAmount;
}
