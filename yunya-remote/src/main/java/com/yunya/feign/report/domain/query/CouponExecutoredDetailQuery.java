package com.yunya.feign.report.domain.query;

import com.yunya.framework.common.model.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 简介: 产品使用明细查询参数
 *
 * @author: chenlin
 * @date: 2021/03/29 14:12
 * @description:
 * @since: 1.0.0
 */
@ApiModel("产品使用明细查询参数")
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
public class CouponExecutoredDetailQuery extends PageQuery implements Serializable {
  /** 门诊ID */
  @ApiModelProperty(value = "门诊ID", required = true)
  @NotNull(message = "门诊ID不能为空")
  private Integer orgId;

  /** 查询时间 */
  @ApiModelProperty(value = "查询开始时间", required = true)
  @NotBlank(message = "查询开始时间不能为空！")
  private String startDate;

  /** 查询结束时间 */
  @ApiModelProperty(value = "查询结束时间", required = true)
  @NotBlank(message = "查询结束时间不能为空！")
  private String endDate;

  /** 产品ID */
  @ApiModelProperty(value = "产品ID", required = true)
  @NotNull(message = "产品ID不能为空")
  private Integer couponId;

  /** 执行人ID */
  @ApiModelProperty(value = "执行人ID", required = true)
  private Integer executorId;

  /** 项目类型 */
  @ApiModelProperty(value = "项目类型", required = true)
  @NotNull(message = "项目类型不能为空")
  private Integer itemType;

  /** 项目ID */
  @ApiModelProperty(value = "项目ID", required = true)
  @NotNull(message = "项目ID不能为空")
  private Integer itemId;

  /** 患者姓名/手机号 */
  @ApiModelProperty(value = "患者/手机号")
  private String keyword;
}
