package com.yunya.feign.treatment.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * 简介: 门诊价目表统一折扣设置参数模型
 *
 * @author: chow
 * @date: 2020/8/6 16:02
 * @description:
 * @since: 1.0.0
 */
@ApiModel("门诊价目表统一折扣设置参数模型")
@Data
@ToString
public class ClinicTariffUniteDiscountForm implements Serializable {

  /** 组织ID */
  @ApiModelProperty(value = "组织ID", required = true)
  @NotNull(message = "组织ID不能为空！")
  private Integer orgId;
  /** 门诊价目表ID数组 */
  @ApiModelProperty(value = "门诊价目表ID数组", required = true)
  @Size(min = 1, message = "门诊价目表ID不能为空！")
  private List<Integer> clinicTariffIds;
  /** 会员折扣信息 */
  @ApiModelProperty(value = "会员折扣信息", required = true)
  @Size(min = 1, message = "会员折扣信息不能为空！")
  private Set<MemberUniteDiscountForm> memberUniteDiscountForms;
}
