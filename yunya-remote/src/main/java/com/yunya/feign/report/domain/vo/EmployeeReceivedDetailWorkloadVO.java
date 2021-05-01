package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介: 已收或免单支付工作量开单明细VO
 *
 * @author: chow
 * @date: 2020/11/30 20:59
 * @description:
 * @since: 1.0.0
 */
@ApiModel("已收或免单支付工作量开单明细VO")
@Data
@ToString
public class EmployeeReceivedDetailWorkloadVO implements Serializable {
  /** 开单明细ID */
  @ApiModelProperty("开单明细ID")
  private Integer orderDetailId;
  /** 项目类型 */
  @ApiModelProperty("项目类型")
  private Byte itemType;
  /** 项目ID */
  @ApiModelProperty("项目ID")
  private Integer itemId;
  /** 项目编号 */
  @ApiModelProperty("项目编号")
  private String itemNum;
  /** 项目名称 */
  @ApiModelProperty("项目名称")
  private String itemName;
  /** 已收工作量 */
  @ApiModelProperty("已收工作量")
  private BigDecimal receivedWorkload;

  /** 免单支付工作量 */
  @ApiModelProperty("免单支付工作量")
  private BigDecimal freePaymentWorkload;
}
