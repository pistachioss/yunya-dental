package com.yunya.feign.treatment.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @program: yunya-dental
 * @description: 就诊中心计数参数模型
 * @author: LHB
 * @create: 2020-11-16 16:10
 */
@Data
@ApiModel(value = "TreatmentCountQuery", description = "就诊中心计数参数模型")
public class TreatmentCountQuery implements Serializable {
  @ApiModelProperty(value = "门诊ID", required = true)
  @NotNull(message = "组织ID不能为空！")
  private Integer orgId;

  @ApiModelProperty(value = "查询日期", required = true, example = "yyyy-MM-dd")
  @NotBlank(message = "查询日期不能为空！")
  private String queryDate;

  @ApiModelProperty(value = "用户ID", required = true)
  @NotNull(message = "用户ID不能为空！")
  private Integer userId;

  @ApiModelProperty(value = "医生ID,助手账号登录情况下，需要传主治医生ID，除此之外可以为空")
  private Integer dentistId;
}
