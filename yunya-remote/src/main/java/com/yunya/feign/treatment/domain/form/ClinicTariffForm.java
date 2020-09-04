package com.yunya.feign.treatment.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 简介: 门诊价目表修改参数模型
 *
 * @author: chow
 * @date: 2020/8/6 13:53
 * @description:
 * @since: 1.0.0
 */
@ApiModel("门诊价目表修改参数模型")
@Data
@ToString
public class ClinicTariffForm implements Serializable {
  /** 基础价目表ID */
  @ApiModelProperty(value = "基础价目表ID", required = true)
  @NotNull(message = "基础价目表ID不能为空！")
  private Integer tariffId;

  /** 门诊价目表单价 */
  @ApiModelProperty(value = "门诊价目表单价", required = true)
  @NotNull(message = "门诊价目表单价不能为空！")
  private BigDecimal price;

  /** 门诊价目表（商品项目）会员卡价格 */
  @ApiModelProperty(value = "门诊价目表（商品项目）会员卡价格", required = true)
  private List<ClinicItemMemberPriceForm> clinicItemMemberPrices;
}
