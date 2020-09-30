package com.yunya.feign.system.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介: 科室信息VO
 *
 * @author: chow
 * @date: 2020/9/29 20:45
 * @description:
 * @since: 1.0.0
 */
@ApiModel("科室信息VO")
@Data
@ToString
public class DeptRoomVO implements Serializable {
  /** 门诊科室ID */
  @ApiModelProperty("门诊科室ID")
  private Integer clinicDeptRoomId;
  /** 科室ID */
  @ApiModelProperty("科室ID")
  private Integer deptRoomId;
  /** 科室名称 */
  @ApiModelProperty("科室名称")
  private String deptRoomName;
  /** 启用状态 */
  @ApiModelProperty("是否启用")
  private Boolean inservice;
}
