package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介: 工作量账单明细VO
 *
 * @author: chow
 * @date: 2021/3/21 15:52
 * @description:
 * @since: 1.0.0
 */
@ApiModel("工作量账单明细VO")
@Data
@ToString
public class BaseBillDetailToWorkloadVO implements Serializable {
  /** 明细ID */
  @ApiModelProperty("明细ID")
  private Integer billDetailId;
  /** 明细工作量 */
  @ApiModelProperty("明细工作量")
  private BigDecimal billDetailWorkload;
  /** 补入工作量 */
  @ApiModelProperty("明细补入工作量")
  private BigDecimal billDetailCouponWorkload;
}
