package com.yunya.modules.system.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介: 门诊科室修改参数模型
 *
 * @author: chow
 * @date: 2020/7/21 13:26
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("门诊科室修改参数模型")
public class ClinicDepartmentRoomForm implements Serializable {
  /** 组织（门诊）ID */
  @ApiModelProperty(value = "组织（门诊）ID ", required = true)
  private Integer orgId;
  /** 科室模版ID */
  @ApiModelProperty("科室模版ID")
  private Integer deptRoomId;
  /** 是否启用 */
  @ApiModelProperty("是否启用")
  private Boolean inservice;
}
