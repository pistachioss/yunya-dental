package com.yunya.feign.treatment.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 简介: 门诊商品项目统一折扣设置参数模型
 *
 * @author: chow
 * @date: 2020/8/6 16:09
 * @description:
 * @since: 1.0.0
 */
@ApiModel("门诊商品项目统一折扣设置参数模型")
@Data
@ToString
public class ClinicOralTariffUniteDiscountForm implements Serializable {

  /** 组织ID */
  @ApiModelProperty(value = "组织ID", required = true)
  @NotNull(message = "组织ID不能为空！")
  private Integer orgId;
  /** 门诊商品项目ID数组 */
  @ApiModelProperty(value = "门诊商品项目ID数组", required = true)
  private List<Integer> clinicOralTariffIds;
  /** 会员折扣信息 */
  @ApiModelProperty(value = "会员折扣信息", required = true)
  private List<MemberUniteDiscountForm> memberUniteDiscountForms;
}
