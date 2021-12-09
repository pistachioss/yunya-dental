package com.yunya.feign.report.domain.query;

import com.yunya.framework.common.model.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介: 员工和门诊查询参数模型
 *
 * @author: chow
 * @date: 2020/10/29 14:52
 * @description:
 * @since: 1.0.0
 */
@ApiModel("员工和门诊查询参数模型")
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
public class MultiClinicEmployeeQuery extends PageQuery implements Serializable {
  /** 组织id列表 */
  @ApiModelProperty(value = "组织id列表"/*, required = true*/)
//  @NotNull(message = "组织ID不能为空！")
  private Integer[] orgIds;

  /** 员工ID列表 */
  @ApiModelProperty("员工ID列表")
  private Integer[] employeeIds;

  /** 就职状态列表：0-试用；1-正式；2-离职；3-实习 */
  @ApiModelProperty("就职状态列表：0-试用；1-正式；2-离职；3-实习")
  private Integer[] workStatus;
}
