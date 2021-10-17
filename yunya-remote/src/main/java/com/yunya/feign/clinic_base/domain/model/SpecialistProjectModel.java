package com.yunya.feign.clinic_base.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 简介: 专科项目新增模型
 *
 * @author: chow
 * @date: 2020/12/22 13:33
 * @description:
 * @since: 1.0.0
 */
@ApiModel("专科项目新增模型")
@Data
@ToString
public class SpecialistProjectModel implements Serializable {
  /** 专科项目名称 */
  @ApiModelProperty(value = "专科项目名称", required = true)
  @NotBlank(message = "专科项目名称不能为空！")
  private String specialistProjectName;
  /** 价目项目组成ID列表 */
  @ApiModelProperty(value = "价目项目组成ID列表")
  private Integer[] tariffItemIds;
  /** 商品项目组成ID列表 */
  @ApiModelProperty(value = "商品项目组成ID列表")
  private Integer[] oralIds;
}
