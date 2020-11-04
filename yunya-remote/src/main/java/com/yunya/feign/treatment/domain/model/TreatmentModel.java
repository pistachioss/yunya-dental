package com.yunya.feign.treatment.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 简介: 开始接诊参数模型
 *
 * @author: chow
 * @date: 2020/10/28 11:22
 * @description:
 * @since: 1.0.0
 */
@ApiModel("开始接诊参数模型")
@Data
@ToString
public class TreatmentModel implements Serializable {
  /** 挂号ID */
  @ApiModelProperty(value = "挂号ID", required = true)
  @NotNull(message = "挂号ID不能为空！")
  private Integer regId;
  /** 接诊人岗位类型 0-助手，1-医生 */
  @ApiModelProperty(value = "接诊人岗位类型 0-助手，1-其他", required = true)
  @NotNull(message = "接诊人岗位类型不能为空！")
  private Byte postType;
}
