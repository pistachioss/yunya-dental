package com.yunya.feign.report.domain.query;

import com.yunya.framework.common.model.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 简介: 员工配诊统计列表查询参数
 *
 * @author: chow
 * @date: 2020/12/2 09:41
 * @description:
 * @since: 1.0.0
 */
@ApiModel("员工配诊统计列表查询参数")
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
public class EmployeeMatchingRecordQuery extends PageQuery implements Serializable {
  /** 门诊ID */
  @ApiModelProperty(value = "门诊ID", required = true)
  @NotNull(message = "门诊ID不能为空！")
  private Integer orgId;
  /** 时间类型 */
  @ApiModelProperty(value = "时间类型：0-日；1-月；2-年", required = true)
  @NotNull(message = "时间类型不能为空！")
  private Byte dateType;
  /** 开始时间 */
  @ApiModelProperty(value = "开始时间", required = true)
  private String startDate;
  /** 结束时间 */
  @ApiModelProperty(value = "结束时间", required = true)
  private String endDate;
  /** 助手列表 */
  private Integer[] assistantIds;
}
