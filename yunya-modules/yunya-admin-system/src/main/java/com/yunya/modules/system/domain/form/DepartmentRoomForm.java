package com.yunya.modules.system.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 简介: 科室参数封装
 *
 * @author: chow
 * @date: 2020/7/20 17:33
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("科室修改参数模型")
public class DepartmentRoomForm implements Serializable {
  /** 科室名称 */
  @ApiModelProperty("科室名称")
  @NotBlank(message = "科室名称不能为空！")
  private String name;
  /** 是否启用 */
  @ApiModelProperty("是否启用")
  private Boolean inservice;
}
