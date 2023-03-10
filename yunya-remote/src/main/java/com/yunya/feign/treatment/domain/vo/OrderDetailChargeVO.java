package com.yunya.feign.treatment.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

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
  /** 开单项目英文名称 */
  @ApiModelProperty("开单项目英文名称")
  private String billingItemEnglishName;
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
  /** 优惠金额 */
  @ApiModelProperty("优惠金额")
  private BigDecimal privilegeAmount;
  /** 牙位 */
  @ApiModelProperty("牙位")
  private String toothBit;
  /** 折扣率 */
  @ApiModelProperty("折扣率")
  private BigDecimal discountRate = BigDecimal.ZERO;
  /** 应收金额 */
  @ApiModelProperty("应收金额（原价-优惠）")
  private BigDecimal actualAmount = BigDecimal.ZERO;
  /** 实收金额 */
  @ApiModelProperty("实收金额")
  private BigDecimal receivedAmount;
  /** 添加来源（0-开单；1-收费） */
  @ApiModelProperty("添加来源（0-开单添加；1-收费（添加商品）添加）")
  private Byte sourceType;
  /** 执行人ID */
  @ApiModelProperty("执行人ID")
  private Integer executorId;
  /** 执行人姓名 */
  @ApiModelProperty("执行人姓名")
  private String executorName;

  @ApiModelProperty("咨询师ID")
  private Integer consulterId;

  @ApiModelProperty("咨询师姓名")
  private String consulterName;

  /** 备注 */
  @ApiModelProperty("备注")
  private String remarks;
  /** 治疗计划详情id列表*/
  @ApiModelProperty("治疗计划详情id列表")
  private List<Integer> planDetailIds;
  /** 优惠适用卡券列表 */
  private List<PrivilegeCouponInfoVO> discountAppliesCoupons;
  /** 会员价 */
  private Map<Integer, Object> memberPrices;

  /** 是否有优惠 */
  @ApiModelProperty("是否有优惠")
  private Boolean hasDiscount = false;
}
