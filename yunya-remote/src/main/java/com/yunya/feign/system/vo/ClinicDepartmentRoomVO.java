package com.yunya.feign.system.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介: 门诊科室VO
 *
 * @author: chow
 * @date: 2020/7/21 09:27
 * @description:
 * @since: 1.0.0
 */
@ApiModel("门诊科室VO")
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
public class ClinicDepartmentRoomVO extends DeptRoomVO implements Serializable {
  /** 组织（诊所）ID */
  @ApiModelProperty("组织（诊所）ID")
  private Integer orgId;
  /** 组织(诊所)简称 */
  @ApiModelProperty("组织(诊所)简称")
  private String orgName;
}
