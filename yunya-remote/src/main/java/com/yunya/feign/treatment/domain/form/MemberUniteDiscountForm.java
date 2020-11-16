package com.yunya.feign.treatment.domain.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
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
@EqualsAndHashCode
public class MemberUniteDiscountForm implements Serializable {
  /** 会员卡类型ID */
  @ApiModelProperty(value = "会员卡类型ID", required = true)
  @NotNull(message = "会员卡类型ID不能为空！")
  private Integer memberTypeId;

  @ApiModelProperty(value = "会员卡折扣率", required = true)
  @NotNull(message = "会员卡折扣率不能为空！")
  @Min(value = 0, message = "会员卡折扣率不能小于0！")
  @Max(value = 100, message = "会员卡折扣率不能大于100！")
  private Float rate;
}
