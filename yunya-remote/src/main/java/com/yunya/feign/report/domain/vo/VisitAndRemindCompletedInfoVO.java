package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介: 随访和提醒完成率信息VO
 *
 * @author: chow
 * @date: 2021/1/15 16:35
 * @description:
 * @since: 1.0.0
 */
@ApiModel("随访和提醒完成率信息VO")
@Data
@ToString
public class VisitAndRemindCompletedInfoVO implements Serializable {
  /** 待完成数量 */
  @ApiModelProperty("待完成数量")
  private Integer waitingForCompletedCount;
  /** 完成数量 */
  @ApiModelProperty("完成数量")
  private Integer completedCount;
  /** 完成百分比 */
  @ApiModelProperty("完成百分比")
  private BigDecimal completedPercentage;
}
