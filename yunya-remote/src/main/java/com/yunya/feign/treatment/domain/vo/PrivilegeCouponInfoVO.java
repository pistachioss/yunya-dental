package com.yunya.feign.treatment.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介: 优惠卡券信息
 *
 * @author: chow
 * @date: 2020/11/5 11:19
 * @description:
 * @since: 1.0.0
 */
@ApiModel("优惠适用卡券（授权折扣）信息")
@Data
@ToString
public class PrivilegeCouponInfoVO implements Serializable {
  /** 卡券ID（授权人ID） */
  @ApiModelProperty("卡券ID（授权人ID）")
  private Integer benefitId;
  /** 卡券类型 */
  @ApiModelProperty(value = "优惠券类型（0：代金券 1：折扣券 2：兑换券 3：套餐券 4：会员卡 5：授权折扣）")
  private Byte couponType;
  /** 卡券名称 */
  @ApiModelProperty("卡券名称(授权人姓名)")
  private String benefitName;
  /** 优惠金额 */
  @ApiModelProperty("优惠金额")
  private BigDecimal benefitAmount;
}
