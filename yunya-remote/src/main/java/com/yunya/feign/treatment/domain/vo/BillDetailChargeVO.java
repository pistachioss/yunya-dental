package com.yunya.feign.treatment.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介: 账单明细VO
 *
 * @author: chow
 * @date: 2021/5/19 18:04
 * @description:
 * @since: 1.0.0
 */
@ApiModel("账单明细VO")
@Data
@ToString
public class BillDetailChargeVO implements Serializable {
  /** 账单明细ID */
  @ApiModelProperty("账单明细ID")
  private Integer orderDetailId;
  /** 项目类型 */
  @ApiModelProperty("项目类型:0-价目表；1-商品")
  private Byte itemType;
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
  @ApiModelProperty("原价")
  private BigDecimal receivableAmount;
  /** 优惠金额 */
  @ApiModelProperty("优惠金额")
  private BigDecimal privilegeAmount;
  /** 牙位 */
  @ApiModelProperty("牙位")
  private String toothBit;
  /** 应收金额 */
  @ApiModelProperty("应收（原价-优惠）")
  private BigDecimal actualAmount;
}
