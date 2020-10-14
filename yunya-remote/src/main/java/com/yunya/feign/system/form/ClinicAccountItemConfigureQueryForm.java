package com.yunya.feign.system.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 简介: 支付方式配置查询参数模型
 *
 * @author: chow
 * @date: 2020/9/29 16:43
 * @description:
 * @since: 1.0.0
 */
@ApiModel("支付方式配置查询参数模型")
@Data
@ToString
public class ClinicAccountItemConfigureQueryForm implements Serializable {
  @ApiModelProperty("是否分页,默认true")
  private Boolean whetherPage = true;

  @ApiModelProperty("页码，默认第一页")
  @Min(message = "最小值", value = 1)
  private Integer pageNum = 1;

  @ApiModelProperty("每页显示条数，默认10条")
  @Min(message = "最小值", value = 1)
  private Integer pageSize = 10;
  /** 入账方式ID */
  @ApiModelProperty(value = "入账方式ID", required = true)
  @NotNull(message = "入账方式ID不能为空！")
  private Integer accountItemId;
}
