package com.yunya.feign.treatment.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 简介: 已完成业务工作目标查询参数模型
 *
 * @author: chow
 * @date: 2020/12/25 17:05
 * @description:
 * @since: 1.0.0
 */
@ApiModel("已完成业务工作目标查询参数模型")
@Data
@ToString
public class CompletedWorkGoalQuery implements Serializable {
  /** 时间类型 */
  @ApiModelProperty(value = "时间类型：0-按月；1-按年", required = true)
  @NotNull(message = "时间类型不能为空！")
  private Byte dateType;
  /** 业务日期 */
  @ApiModelProperty(value = "业务日期", required = true)
  @NotBlank(message = "业务日期不能为空！")
  private String businessDate;
  /** 门诊ID列表 */
  @ApiModelProperty(value = "数据所属ID列表（门诊ID或用户ID）", required = true)
  @NotEmpty(message = "诊所ID不能为空！")
  private Integer[] belongIds;
}
