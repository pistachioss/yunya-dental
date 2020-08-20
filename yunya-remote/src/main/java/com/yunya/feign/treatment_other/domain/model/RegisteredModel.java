package com.yunya.feign.treatment_other.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 简介: 患者挂号新增参数模型
 *
 * @author: chow
 * @date: 2020/8/11 11:29
 * @description:
 * @since: 1.0.0
 */
@ApiModel("患者挂号新增参数模型")
@Data
@ToString
public class RegisteredModel implements Serializable {

  /** 患者id */
  @ApiModelProperty(value = "患者id", required = true)
  @NotNull(message = "患者id不能为空！")
  private Integer patientId;

  /** 预约id */
  @ApiModelProperty("预约id")
  private Integer appointmentId;

  /** 挂号医生id */
  @ApiModelProperty(value = "挂号医生id", required = true)
  @NotNull(message = "挂号医生id不能为空！")
  private Integer dentistId;

  /** 助手id */
  @ApiModelProperty("助手id")
  private Integer assistantId;

  /** 科室ID */
  @ApiModelProperty("科室ID")
  private Integer deptRoomId;

  /** 挂号备注 */
  @ApiModelProperty("挂号备注")
  private String remark;
}
