package com.yunya.feign.treatment.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介: 退费付款方式VO
 *
 * @author: chow
 * @date: 2020/9/27 20:19
 * @description:
 * @since: 1.0.0
 */
@ApiModel("退费付款方式VO")
@Data
@ToString
public class BillRefundPaymentVO implements Serializable {
  /** 账单退费记录ID */
  @ApiModelProperty("账单退费付款明细记录ID")
  private Integer billRefundPayDetailRecordId;
  /** 退费付款方式ID */
  @ApiModelProperty("退费付款方式ID")
  private Integer accountItemId;
  /** 退费方式名称 */
  @ApiModelProperty("退费付款方式名称")
  private String accountItemName;
  /** 退费金额合计 */
  @ApiModelProperty("退费金额合计")
  private BigDecimal refundPayAmount;
  /** 退费本金 */
  @ApiModelProperty("退费")
  private BigDecimal principalAmount;
  /** 退费赠金 */
  @ApiModelProperty("退费赠金")
  private BigDecimal giftAmount;
  /** 备注 */
  @ApiModelProperty("备注")
  private String remark;
}
