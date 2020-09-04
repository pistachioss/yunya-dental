package com.yunya.feign.treatment.domain.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Min;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介: 门诊项目修改参数模型
 *
 * @author: chow
 * @date: 2020/8/3 16:52
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
public class ClinicItemPriceForm implements Serializable {
  @ApiModelProperty("门诊项目（商品/价目表）ID")
  private Integer clinicItemId;
  /** 诊所ID */
  @ApiModelProperty(value = "组织ID", required = true)
  private Integer orgId;
  /** 项目ID */
  @ApiModelProperty(value = "项目（商品项目/价目表项目）单价", required = true)
  @Min(value = 0, message = "价格不能小于0！")
  private BigDecimal itemPrice;
  /** 是否启用 */
  @ApiModelProperty(value = "是否启用", required = true)
  private Boolean itemInservice;
}
