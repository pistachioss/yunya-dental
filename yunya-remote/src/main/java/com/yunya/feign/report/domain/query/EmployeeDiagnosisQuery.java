package com.yunya.feign.report.domain.query;

import com.yunya.framework.common.model.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * 简介: 员工看诊情况查询参数
 *
 * @author: chow
 * @date: 2020/12/4 20:41
 * @description:
 * @since: 1.0.0
 */
@ApiModel("员工看诊情况查询参数")
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
public class EmployeeDiagnosisQuery extends PageQuery implements Serializable {
  /** 组织ID列表 */
  @ApiModelProperty(value = "组织ID列表", required = true)
  @NotNull(message = "组织ID列表不能为空！")
  private List<Integer> orgIds;
  /** 组织ID */
  private Integer orgId;
  /** 查询开始日期 */
  @ApiModelProperty(value = "查询开始日期", example = "yyyy-MM-dd", required = true)
  @NotBlank(message = "查询开始日期不能为空！")
  private String startDate;
  /** 查询结束日期 */
  @ApiModelProperty(value = "查询结束日期", example = "yyyy-MM-dd", required = true)
  @NotBlank(message = "查询结束日期不能为空！")
  private String endDate;
  /** 医生ID列表 */
  @ApiModelProperty("医生ID列表")
  private Integer[] dentistIds;
  /**就职状态列表：0-试用；1-正式；2-离职；3-实习*/
  @ApiModelProperty("就职状态列表：0-试用；1-正式；2-离职；3-实习")
  private Collection<Integer> workStatus;
}
