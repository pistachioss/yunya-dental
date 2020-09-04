package com.yunya.feign.treatment.domain.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介: 价目表/商品项目会员卡价参数封装模型
 *
 * @author: chow
 * @date: 2020/8/6 13:59
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
public class ClinicItemMemberPriceForm implements Serializable {
  /** 会员卡类型ID */
  @ApiModelProperty(value = "会员卡类型ID", required = true)
  private Integer memberTypeId;
  /** 会员卡价格 */
  @ApiModelProperty(value = "会员卡价格", required = true)
  private BigDecimal discountPrice;
}
