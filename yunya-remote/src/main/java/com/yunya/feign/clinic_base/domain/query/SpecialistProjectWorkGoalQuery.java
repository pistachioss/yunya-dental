package com.yunya.feign.clinic_base.domain.query;

import com.yunya.framework.common.model.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 简介: 专科项目工作目标查询参数模型
 *
 * @author: chow
 * @date: 2020/12/29 14:56
 * @description:
 * @since: 1.0.0
 */
@ApiModel("专科项目工作目标查询参数模型")
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
public class SpecialistProjectWorkGoalQuery extends PageQuery implements Serializable {
  /** 时间类型 */
  @ApiModelProperty(value = "时间类型：0-按月；1-按年", required = true)
  @NotNull(message = "时间类型不能为空！")
  private Byte dateType;
  /** 开始时间 */
  @ApiModelProperty(value = "开始时间", required = true)
  @NotBlank(message = "开始时间不能为空！")
  private String startDate;
  /** 结束时间 */
  @ApiModelProperty(value = "结束时间", required = true)
  @NotBlank(message = "结束时间不能为空！")
  private String endDate;
  /** 数据所属类型 */
  @ApiModelProperty(value = "数据所属类型;0-组织，1-个人", required = true)
  @NotNull(message = "数据所属类型不能为空！")
  private Byte belongType;
  /** 门诊ID列表 */
  @ApiModelProperty(value = "数据所属ID列表（门诊ID或用户ID）", required = true)
  @NotEmpty(message = "诊所ID不能为空！")
  private Integer[] belongIds;
  /** 专科项目ID列表 */
  @ApiModelProperty(value = "专科项目ID列表", required = true)
  @NotEmpty(message = "专科项目ID列表不能为空！")
  private Integer[] specialistProjectIds;
}
