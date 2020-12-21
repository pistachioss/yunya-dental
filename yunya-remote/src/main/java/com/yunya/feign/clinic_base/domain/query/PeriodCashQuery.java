package com.yunya.feign.clinic_base.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 简介: 期间现金收款查询参数
 *
 * @author: chow
 * @date: 2020/12/21 10:48
 * @description:
 * @since: 1.0.0
 */
@ApiModel("期间现金收款查询参数")
@Data
@ToString
public class PeriodCashQuery implements Serializable {
  /** 组织ID */
  @ApiModelProperty(value = "组织ID", required = true)
  @NotNull(message = "组织ID不能为空！")
  private Integer orgId;
  /** 结存日期 */
  @ApiModelProperty(value = "结存日期", required = true, example = "yyyy-MM-dd")
  @NotBlank(message = "结存日期不能为空！")
  private String settlementDate;
}
