package com.yunya.feign.treatment.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介: 账单退费开单详情VO
 *
 * @author: chow
 * @date: 2020/9/27 19:28
 * @description:
 * @since: 1.0.0
 */
@ApiModel("账单退费开单详情VO")
@Data
@ToString
public class BillRefundOrderDetailVO implements Serializable {
  /** 退费开单详情记录ID */
  @ApiModelProperty("退费开单详情记录ID")
  private Integer id;
  /** 开单详情ID */
  @ApiModelProperty("开单详情ID")
  private Integer orderDetailId;
  /** 开单类型（0-价目表；1-商品；） */
  @ApiModelProperty("开单类型（0-价目表；1-商品；）")
  private Byte type;
  /** 开单项目ID */
  @ApiModelProperty("开单项目ID")
  private Integer billingItemId;
  /** 开单项目名称 */
  @ApiModelProperty("开单项目名称")
  private String billingItemName;
  /** 单位 */
  @ApiModelProperty("单位")
  private String unit;
  /** 门诊价目表单价 */
  @ApiModelProperty("门诊价目表单价")
  private BigDecimal price;
  /** 数量 */
  @ApiModelProperty("数量")
  private Integer quantity;
  /** 牙位 */
  @ApiModelProperty("牙位")
  private String toothBit;
  /** 执行人ID */
  @ApiModelProperty("执行人ID")
  private Integer executorId;
  /** 执行人姓名 */
  @ApiModelProperty("执行人姓名")
  private String executorName;
  /** 备注 */
  @ApiModelProperty("备注")
  private String remarks;
  /** 已收金额 */
  @ApiModelProperty("实收金额")
  private BigDecimal receivedAmount;
  /** 退费金额 */
  @ApiModelProperty("退费金额")
  private BigDecimal refundAmount;
}
