package com.yunya.feign.treatment.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 简介: 普通优惠参数模型
 *
 * @author: chow
 * @date: 2020/8/27 15:25
 * @description:
 * @since: 1.0.0
 */
@ApiModel("普通优惠参数模型")
@Data
@ToString
public class GeneralDiscountModel implements Serializable {

  /** 会员卡类型id */
  @ApiModelProperty("会员卡类型ID")
  private Integer memberTypeId;

  /** 患者卡券信息 */
  private List<CouponDiscountInfoModel> couponDiscountInfoModels;
}
