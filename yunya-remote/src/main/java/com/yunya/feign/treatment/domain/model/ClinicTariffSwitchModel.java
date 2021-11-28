package com.yunya.feign.treatment.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 简介: 门诊价目表（商品）启用设置模型
 *
 * @author: chow
 * @date: 2020/11/14 15:39
 * @description:
 * @since: 1.0.0
 */
@ApiModel("门诊价目表（商品）启用设置模型")
@Data
@ToString
public class ClinicTariffSwitchModel implements Serializable {
  /** 门诊ID */
  @ApiModelProperty(value = "门诊ID", required = true)
  @NotNull(message = "门诊ID不能为空！")
  private Integer orgId;
  /** 价目表分类ID列表 */
  @ApiModelProperty(value = "价目表分类ID列表", required = true)
  @NotNull(message = "请选择一个价目表分类！")
  private Integer tariffCategoryId;
  /** 可用/不可用 */
  @ApiModelProperty(value = "可用-true/不可用-false", required = true)
  @NotNull(message = "请选择启用或禁用！")
  private Boolean isAvailable;
}
