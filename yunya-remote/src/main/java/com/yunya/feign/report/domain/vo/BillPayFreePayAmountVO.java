package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介: 收费记录免单金额VO
 *
 * @author: chow
 * @date: 2021/3/22 13:08
 * @description:
 * @since: 1.0.0
 */
@ApiModel("收费记录免单金额VO")
@Data
@ToString
public class BillPayFreePayAmountVO implements Serializable {
  /** 收费记录ID */
  @ApiModelProperty("收费记录ID")
  private Integer billPayId;
  /** 账单ID */
  @ApiModelProperty("账单ID")
  private Integer billId;
  /** 免单合计 */
  @ApiModelProperty("免单合计")
  private BigDecimal freePayAmount;
}
