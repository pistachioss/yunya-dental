package com.yunya.feign.report.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 简介: 门诊对账单查询参数
 *
 * @author: chow
 * @date: 2020/12/10 16:00
 * @description:
 * @since: 1.0.0
 */
@ApiModel("门诊对账单查询参数")
@Data
@ToString
public class InboundAndOutboundStatementQuery implements Serializable {
  /** 组织ID */
  @ApiModelProperty(value = "组织ID", required = true)
  @NotNull(message = "组织ID")
  private Integer orgId;
  /** 时间类型 */
  @ApiModelProperty(value = "时间类型：0-日；1-月；2-年", required = true)
  @NotNull(message = "时间类型不能为空！")
  private Byte dateType;
  /** 开始时间 */
  @ApiModelProperty(value = "开始时间", required = true)
  @NotBlank(message = "查询开始时间不能为空！")
  private String startDate;
  /** 结束时间 */
  @ApiModelProperty(value = "结束时间", required = true)
  @NotBlank(message = "查询结束时间不能为空！")
  private String endDate;
}
