package com.yunya.feign.report.domain.query;

import com.yunya.framework.common.model.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Collection;

/**
 * 简介: 产品使用查询参数
 *
 * @author: chenlin
 * @date: 2021/03/29 14:12
 * @description:
 * @since: 1.0.0
 */
@ApiModel("产品使用查询参数")
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
public class CouponExecutoredQuery extends PageQuery implements Serializable {
  /** 门诊ID列表 */
  @ApiModelProperty(value = "门诊ID列表")
  private Collection<Integer> orgIds;

  /** 查询时间 */
  @ApiModelProperty(value = "查询开始时间", required = true)
  @NotBlank(message = "查询开始时间不能为空！")
  private String startDate;

  /** 查询结束时间 */
  @ApiModelProperty(value = "查询结束时间", required = true)
  @NotBlank(message = "查询结束时间不能为空！")
  private String endDate;

  /** 产品ID列表 */
  @ApiModelProperty(value = "产品ID列表")
  private Collection<Integer> couponIds;

  /** 执行人姓名 */
  @ApiModelProperty(value = "执行人姓名")
  private String executorName;

  /** 就职状态 */
  @ApiModelProperty(value = "就职状态")
  private Collection<Byte> workStatus;
}
