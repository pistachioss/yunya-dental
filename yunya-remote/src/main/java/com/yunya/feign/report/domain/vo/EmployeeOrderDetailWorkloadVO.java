package com.yunya.feign.report.domain.vo;

import com.yunya.models.report.BaseBill;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介: 员工工作量开单明细VO
 *
 * @author: chow
 * @date: 2020/11/30 15:45
 * @description:
 * @since: 1.0.0
 */
@ApiModel("员工工作量开单明细VO")
@Data
@ToString
public class EmployeeOrderDetailWorkloadVO implements Serializable {
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
  /** 原价 */
  @ApiModelProperty("原价")
  private BaseBill originalPrice;
  /** 优惠金额 */
  @ApiModelProperty("优惠金额")
  private BigDecimal discountAmount;
  /** 实收工作量 */
  @ApiModelProperty("实收工作量")
  private BigDecimal actualWorkload;
}
