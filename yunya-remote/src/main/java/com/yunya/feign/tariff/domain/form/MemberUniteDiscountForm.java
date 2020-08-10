package com.yunya.feign.tariff.domain.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介: 门诊价目表/商品项目会员价统一设置参数模型
 *
 * @author: chow
 * @date: 2020/8/6 15:56
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
public class MemberUniteDiscountForm implements Serializable {
  /** 会员卡类型ID */
  @ApiModelProperty(value = "会员卡类型ID", required = true)
  private Integer memberTypeId;

  @ApiModelProperty(value = "会员卡折扣率", required = true)
  private Float rate;
}
