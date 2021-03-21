package com.yunya.feign.report.domain.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介: 门诊账单组合信息VO（账单实收总额、账单工作量总额、账单免单总额、账单补入工作量总额）
 *
 * @author: chow
 * @date: 2021/3/18 10:13
 * @description:
 * @since: 1.0.0
 */
@ApiModel("门诊账单和收费组合信息VO")
@Data
@ToString
public class ClinicBillAndPayGroupInfoVO implements Serializable {
  /** 账单补入工作量总额 */
  @ApiModelProperty("账单补入工作量总额")
  private BigDecimal totalBillCouponWorkload;
  /** 账单免单支付总额 */
  @ApiModelProperty("账单免单支付总额")
  private BigDecimal totalBillFreePayAmount;
  /** 门诊已收工作量总额 */
  @ApiModelProperty("门诊已收工作量总额")
  private BigDecimal totalClinicReceivedWorkload;
}
