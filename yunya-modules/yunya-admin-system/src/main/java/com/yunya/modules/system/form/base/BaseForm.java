package com.yunya.modules.system.form.base;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 简单介绍:</br> 公共参数封装form
 *
 * @author: chow
 * @date: 2020/5/28 14:48
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("数据编辑公共参数模型")
public class BaseForm implements Serializable {
  /** 品牌名称 */
  @NotBlank(message = "名称不能为空！")
  @Size(max = 50, message = "名称长度不能超过50个字符！")
  @ApiModelProperty(value = "名称", required = true)
  private String name;
  /** 数据类型 */
  @ApiModelProperty("类型0-门诊；1-公司")
  private Byte type;
  /** 自定义排序 */
  @ApiModelProperty("排序")
  private Integer orderNum;
  /** 是否启用 */
  @ApiModelProperty("是否启用")
  private Boolean inservice;
}
