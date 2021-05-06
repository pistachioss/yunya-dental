package com.yunya.feign.patient_central.domain.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简单介绍:</br> 会员卡退费Model
 *
 * @author: WY
 * @date 2020/8/26 14:44
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
public class MemberReturnRecordModel implements Serializable {

  /** 会员卡id */
  @NotNull(message = "会员卡id不能为空")
  @ApiModelProperty(value = "会员卡id", required = true)
  private String memberId;

  /** 患者id */
  @NotNull(message = "患者id不能为空")
  @ApiModelProperty(value = "患者id", required = true)
  private Integer patientId;

  /** 退还本金 */
  @ApiModelProperty(value = "退还本金", required = false)
  @Size(message = "金额不能小于0")
  private BigDecimal returnPrincipalAmount;

  /** 退还赠金 */
  @ApiModelProperty(value = "退还赠金", required = false)
  @Size(message = "金额不能小于0")
  private BigDecimal returnGiftAmount;

  /** 退费方式ID */
  @ApiModelProperty(value = "退费方式ID", required = false)
  private Integer returnWayId;

  /** 退费支付金额 */
  @ApiModelProperty("退费支付金额")
  @Size(message = "金额不能小于0")
  private BigDecimal returnPayAmount;

  /** 退费原因 */
  @ApiModelProperty(value = "退费原因")
  private String returnReason;
}
