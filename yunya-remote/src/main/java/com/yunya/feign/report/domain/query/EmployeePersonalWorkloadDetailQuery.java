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

/**
 * 简介: 员工个人工作量明细查询参数
 *
 * @author: chow
 * @date: 2020/11/28 16:44
 * @description:
 * @since: 1.0.0
 */
@ApiModel("员工个人工作量明细查询参数")
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
public class EmployeePersonalWorkloadDetailQuery extends PageQuery implements Serializable {
  /** 组织ID */
  @ApiModelProperty(value = "组织ID", required = true)
  @NotNull(message = "组织ID不能为空！")
  private Integer orgId;
  /** 时间类型 */
  @ApiModelProperty(value = "时间类型:0-日（yyyy-MM-dd）；1-月(yyyy-MM)；2-年(yyyy)", required = true)
  @NotNull(message = "时间类型不能为空！")
  private Byte dateType;
  /** 查询时间 */
  @ApiModelProperty(value = "查询开始时间", required = true)
  @NotBlank(message = "查询开始时间不能为空！")
  private String startDate;
  /** 查询时间 */
  @ApiModelProperty(value = "查询结束时间", required = true)
  @NotBlank(message = "查询结束时间不能为空！")
  private String endDate;
  /** 员工ID */
  @ApiModelProperty(value = "员工ID", required = true)
  @NotNull(message = "员工ID不能为空！")
  private Integer employeeId;
  /** 患者关键字 */
  @ApiModelProperty("患者关键字：患者姓名、姓名拼音、手机号")
  private String keyword;
  /** 账单编号 */
  @ApiModelProperty("账单编号")
  private String billNum;
  /** 开单日期 */
  @ApiModelProperty("开单日期")
  private String orderDate;
  /** 账单日期 */
  @ApiModelProperty("账单日期")
  private String billDate;
}
