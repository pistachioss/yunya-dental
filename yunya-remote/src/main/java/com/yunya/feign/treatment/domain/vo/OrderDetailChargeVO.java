package com.yunya.feign.treatment.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 简介: 收费订单详情VO
 *
 * @author: chow
 * @date: 2020/11/5 10:30
 * @description:
 * @since: 1.0.0
 */
@ApiModel("收费订单详情VO")
@Data
@ToString
public class OrderDetailChargeVO implements Serializable {
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
  /** 应收原价合计 */
  @ApiModelProperty("应收原价合计（原价）")
  private BigDecimal receivableAmount;
  /** 牙位 */
  @ApiModelProperty("牙位")
  private String toothBit;
  /** 折扣率 */
  @ApiModelProperty("折扣率")
  private BigDecimal discountRate;
  /** 实收金额 */
  @ApiModelProperty("实收金额（原价-优惠）")
  private BigDecimal actualAmount;
  /** 添加来源（0-开单；1-收费） */
  @ApiModelProperty("添加来源（0-开单添加；1-收费（添加商品）添加）")
  private Byte sourceType;
  /** 执行人ID */
  @ApiModelProperty("执行人ID")
  private Integer executorId;
  /** 执行人姓名 */
  @ApiModelProperty("执行人姓名")
  private String executorName;
  /** 备注 */
  @ApiModelProperty("备注")
  private String remarks;
  /** 优惠适用卡券列表 */
  private List<PrivilegeCouponInfoVO> discountAppliesCoupons;
}
