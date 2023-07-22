package com.yunya.feign.treatment.domain.vo;

import com.google.common.collect.Lists;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

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
public class OrderDetailChargeVO extends OrderDetailVO {
  /** 开单项目英文名称 */
  @ApiModelProperty("开单项目英文名称")
  private String billingItemEnglishName;
  /** 优惠金额 */
  @ApiModelProperty("优惠金额")
  private BigDecimal privilegeAmount;
  /** 折扣率 */
  @ApiModelProperty("折扣率：应收/原价*100%")
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
  /** 优惠适用卡券列表 */
  private List<PrivilegeCouponInfoVO> discountAppliesCoupons = Lists.newArrayList();

  /** 是否有优惠 */
  @ApiModelProperty("是否有优惠")
  private Boolean hasDiscount = false;

  @Override
  public OrderDetailChargeVO clone() throws CloneNotSupportedException {
    return (OrderDetailChargeVO) super.clone();
  }
}
