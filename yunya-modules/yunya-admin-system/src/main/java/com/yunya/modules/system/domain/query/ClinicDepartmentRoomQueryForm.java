package com.yunya.modules.system.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Min;
import java.io.Serializable;

/**
 * 简介: 门诊科室查询参数模型
 *
 * @author: chow
 * @date: 2020/7/20 21:03
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("门诊科室查询参数模型")
public class ClinicDepartmentRoomQueryForm implements Serializable {
  @ApiModelProperty(value = "是否分页", required = true)
  private Boolean whetherPage = true;
  /** 查询页码 */
  @ApiModelProperty("页码")
  @Min(message = "页码最小值最小值为1", value = 1)
  private Integer pageNum = 1;
  /** 每页显示条数 */
  @ApiModelProperty("每页显示数量")
  @Min(message = "每页显示条数最小值为1", value = 1)
  private Integer pageSize = 10;
  /** 门诊科室ID */
  @ApiModelProperty("门诊科室ID")
  private Integer clinicDeptRoomId;
  /** (组织)门诊ID */
  @ApiModelProperty("(组织)门诊ID")
  private Integer orgId;
  /** 科室ID */
  @ApiModelProperty("科室ID")
  private Integer deptRoomId;
  /** 是否启用 */
  @ApiModelProperty("是否启用(默认启用)，门诊科室如需过滤不启用，传true")
  private Boolean inservice = true;
}
