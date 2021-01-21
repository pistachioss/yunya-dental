package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介: 账单收费记录VO
 *
 * @author: chow
 * @date: 2021/1/21 17:03
 * @description:
 * @since: 1.0.0
 */
@ApiModel("账单收费记录VO")
@Data
@ToString
public class BaseBillPayVO implements Serializable {
  /** 账单收费记录ID */
  @ApiModelProperty("账单收费记录ID")
  private Integer billPayId;
  /** 收费日期 */
  @ApiModelProperty("收费日期")
  private String payeeDate;
  /** 本次收费金额 */
  @ApiModelProperty("本次收费金额")
  private BigDecimal receivedAmount;
  /** 收费人ID */
  @ApiModelProperty("收费人ID")
  private Integer payeeId;
  /** 收费人姓名 */
  @ApiModelProperty("收费人姓名")
  private String payeeName;
}
