package com.yunya.modules.system.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Min;
import java.io.Serializable;

/**
 * 简介: 入账方式列表查询参数模型
 *
 * @author: chow
 * @date: 2020/7/27 10:46
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("入账方式列表查询参数模型")
public class AccountItemQueryForm implements Serializable {
  @ApiModelProperty(value = "是否分页,默认分页")
  private Boolean whetherPage = true;

  @ApiModelProperty("页码，默认第1页")
  @Min(message = "最小值", value = 1)
  private Integer pageNum = 1;

  @ApiModelProperty("每页显示数量，默认显示10条")
  @Min(message = "最小值", value = 1)
  private Integer pageSize = 10;
  /** 入账方式ID */
  @ApiModelProperty("入账方式ID")
  private Integer id;
  /** 入账方式分类ID */
  @ApiModelProperty("入账方式分类ID")
  private Integer accountTypeId;
  /** 入账方式名称 */
  @ApiModelProperty("入账方式名称")
  private String name;
  /** 入账方式类型 */
  @ApiModelProperty("入账方式类型(可传多个，example:[0,1])")
  private Byte[] types;
  /** 是否启用 */
  @ApiModelProperty("是否启用")
  private Boolean inservice;
}
