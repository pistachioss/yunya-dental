package com.yunya.feign.tariff.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Min;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介: 门诊项目（商品/处置价目表）单价新增模型
 *
 * @author: chow
 * @date: 2020/8/3 10:52
 * @description:
 * @since: 1.0.0
 */
@ApiModel("门诊项目（商品/处置价目表）单价新增模型")
@Data
@ToString
public class ClinicItemPriceModel implements Serializable {
  /** 诊所ID */
  @ApiModelProperty(value = "组织ID", required = true)
  private Integer orgId;
  /** 项目ID */
  @ApiModelProperty(value = "项目（商品项目/价目表项目）单价", required = true)
  @Min(value = 0, message = "价格不能小于0！")
  private BigDecimal itemPrice;
}
