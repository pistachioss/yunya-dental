package com.yunya.modules.system.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 简介: 品牌新增参数模型
 *
 * @author: chow
 * @date: 2020/7/27 17:55
 * @description:
 * @since: 1.0.0
 */
@ApiModel("品牌新增参数模型")
@Data
@ToString
public class BrandModel implements Serializable {

  @NotBlank(message = "品牌名称为空")
  @Size(max = 50, message = "品牌名称不能超过50个字符")
  @ApiModelProperty(value = "品牌名称", required = true)
  private String name;

  /** 自定义排序 */
  @ApiModelProperty("自定义排序")
  private Integer orderNum;
}
