package com.yunya.modules.system.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 简介: 科室新增参数模型
 *
 * @author: chow
 * @date: 2020/7/27 19:29
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("科室新增参数模型")
public class DepartmentRoomModel implements Serializable {
  /** 科室名称 */
  @ApiModelProperty("科室名称")
  @NotBlank(message = "科室名称不能为空！")
  @Size(max = 50, message = "科室名称长度不能超过50个字符")
  private String name;
}
