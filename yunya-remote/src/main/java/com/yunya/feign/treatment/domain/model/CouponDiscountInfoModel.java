package com.yunya.feign.treatment.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 简介: 卡券优惠参数模型
 *
 * @author: chow
 * @date: 2020/8/27 16:33
 * @description:
 * @since: 1.0.0
 */
@ApiModel("卡券优惠参数模型")
@Data
@ToString
public class CouponDiscountInfoModel implements Serializable {

  /** 卡券信息ID */
  @ApiModelProperty(value = "卡券公共信息ID", required = true)
  @NotNull(message = "卡券信息ID不能为空！")
  private Integer couponCommonInfoId;
  /** 卡券类型 */
  @ApiModelProperty(value = "卡券类型", required = true)
  @NotNull(message = "卡券分类不能为空！")
  private Byte couponType;
}
