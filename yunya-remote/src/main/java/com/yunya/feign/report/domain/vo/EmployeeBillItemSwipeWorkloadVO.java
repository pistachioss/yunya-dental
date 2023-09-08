package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介: 员工划扣工作量项目VO
 *
 * @author: chow
 * @date: 2020/11/30 15:45
 * @description:
 * @since: 1.0.0
 */
@ApiModel("员工划扣工作量项目VO")
@Data
@ToString
public class EmployeeBillItemSwipeWorkloadVO implements Serializable {
  @ApiModelProperty("订单明细id")
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
  /** 划扣工作量 */
  @ApiModelProperty("划扣工作量")
  private BigDecimal swipeWorkload;
}
