package com.yunya.feign.treatment.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介: 会员卡统一价格参数模型
 *
 * @author: chow
 * @date: 2021/9/23 17:13
 * @description:
 * @since: 1.0.0
 */
@Data
@ApiModel("会员卡统一价格参数模型")
public class MemberUnitePriceForm implements Serializable {
  /** 会员卡类型ID */
  @ApiModelProperty(value = "会员卡类型ID", required = true)
  @NotNull(message = "会员卡类型ID不能为空！")
  private Integer memberTypeId;
  /** 会员卡价格 */
  @ApiModelProperty(value = "会员卡价格", required = true)
  @NotNull(message = "会员价不能为空！")
  @Min(value = 0, message = "会员价不能小于0！")
  private BigDecimal memberPrice;
}
