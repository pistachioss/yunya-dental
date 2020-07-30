package com.yunya.modules.system.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 简介: 门诊科室参数模型
 *
 * @author: chow
 * @date: 2020/7/20 20:35
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("门诊科室参数模型")
public class ClinicDepartmentRoomModel implements Serializable {
  /** 组织ID */
  @ApiModelProperty(value = "组织ID", required = true)
  @NotNull(message = "组织ID（诊所ID）不能为空！")
  private Integer orgId;
  /** 科室ID */
  @ApiModelProperty(value = "科室ID", required = true)
  @NotNull(message = "科室ID不能为空！")
  private Integer departmentRoomId;
}
