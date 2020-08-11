package com.yunya.feign.tariff.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 简介: 门诊商品项目修改参数模型
 *
 * @author: chow
 * @date: 2020/8/6 13:54
 * @description:
 * @since: 1.0.0
 */
@ApiModel("门诊商品项目修改参数模型")
@Data
@ToString
public class ClinicOralTariffForm implements Serializable {
  /** 商品项目ID */
  @ApiModelProperty(value = "商品项目ID", required = true)
  @NotNull(message = "商品项目ID不能为空！")
  private Integer oralTariffId;

  /** 门诊价目表单价 */
  @ApiModelProperty(value = "门诊商品项目单价", required = true)
  @NotNull(message = "门诊商品项目单价不能为空！")
  private BigDecimal price;

  /** 门诊价目表（商品项目）会员卡价格 */
  @ApiModelProperty("门诊价目表（商品项目）会员卡价格")
  private List<ClinicItemMemberPriceForm> clinicItemMemberPrices;
}
