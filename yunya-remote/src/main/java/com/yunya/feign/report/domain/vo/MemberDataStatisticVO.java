package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介: 门诊会员数据统计VO
 *
 * @author: chow
 * @date: 2020/12/8 15:26
 * @description:
 * @since: 1.0.0
 */
@ApiModel("门诊会员数据统计VO")
@Data
@ToString
public class MemberDataStatisticVO implements Serializable {
  /** 会员卡充值总额（本金+赠金） */
  @ApiModelProperty("会员卡充值总额（本金+赠金）")
  private BigDecimal totalMemberRechargeAmount;
  /** 会员卡消费总额（本金+赠金） */
  @ApiModelProperty("会员卡消费总额（本金+赠金）")
  private BigDecimal totalMemberExpendAmount;
  /** 会员卡退费总额（本金+赠金） */
  @ApiModelProperty("会员卡退费总额（本金+赠金）")
  private BigDecimal totalMemberRefundAmount;
}
