package com.yunya.feign.report.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介: 员工工作量消耗成本添加参数模型
 *
 * @author: chow
 * @date: 2020/11/3 20:36
 * @description:
 * @since: 1.0.0
 */
@ApiModel("员工工作量消耗成本添加参数模型")
@Data
@ToString
public class EmployeeWorkloadCostModel implements Serializable {
  /** 组织id*/
  @ApiModelProperty(value = "组织id")
  @NotNull(message = "组织id不能为空！")
  private Integer orgId;
  /** 员工ID */
  @ApiModelProperty(value = "员工(用户)ID", required = true)
  @NotNull(message = "员工(用户)ID不能为空！")
  private Integer employeeId;
  /** 键入年月 */
  @ApiModelProperty(value = "键入年月", required = true, example = "yyyy-MM")
  @NotBlank(message = "输入工作量成本消耗的年月不能为空！")
  private String entryMonth;
  /** 加工费 */
  @ApiModelProperty("加工费")
  @Min(value = 0, message = "不能输入小于0的金额！")
  private BigDecimal processingFee;
  /** 正畸加工费 */
  @ApiModelProperty("正畸加工费")
  @Min(value = 0, message = "不能输入小于0的金额！")
  private BigDecimal orthodonticsFee;
  /** 基本工作量 */
  @ApiModelProperty("基本工作量")
  @Min(value = 0, message = "不能输入小于0的金额！")
  private BigDecimal baseWorkload;
  /** 大额材料费 */
  @ApiModelProperty("大额材料费")
  @Min(value = 0, message = "不能输入小于0的金额！")
  private BigDecimal materialFee;
}
