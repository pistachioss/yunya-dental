package com.yunya.feign.treatment.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 简介: APP端就诊列表查询参数模型
 *
 * @author: chow
 * @date: 2020/10/11 15:51
 * @description:
 * @since: 1.0.0
 */
@ApiModel(value = "AppTreatListQuery",description = "APP端就诊列表查询参数模型")
@Data
@ToString
public class AppTreatListQuery implements Serializable {
  @ApiModelProperty("是否分页,默认true")
  private Boolean whetherPage = true;

  @ApiModelProperty("页码，默认第一页")
  @Min(message = "最小值", value = 1)
  private Integer pageNum = 1;

  @ApiModelProperty("每页显示条数，默认10条")
  @Min(message = "最小值", value = 1)
  private Integer pageSize = 10;
  /** 组织ID */
  @ApiModelProperty(value = "组织ID", required = true)
  @NotNull(message = "组织ID不能为空！")
  private Integer orgId;
  /** 查询日期 */
  @ApiModelProperty(value = "查询日期", example = "yyyy-MM-dd", required = true)
  @NotBlank(message = "查询日期不能为空！")
  private String queryDate;
  /** 医生ID */
  @ApiModelProperty("医生ID")
  private Integer dentistId;
}
