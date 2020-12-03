package com.yunya.feign.treatment.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介: 助手配诊统计VO
 *
 * @author: chow
 * @date: 2020/12/2 11:01
 * @description:
 * @since: 1.0.0
 */
@ApiModel("助手配诊统计VO")
@Data
@ToString
public class AssistantMatchingStatisticsVO implements Serializable {
  /** 组织ID */
  private Integer orgId;
  /** 员工ID */
  @ApiModelProperty("员工ID")
  private Integer assistantId;
  /** 员工姓名 */
  @ApiModelProperty("员工姓名")
  private String assistantName;
  /** 助手1配诊时长 */
  @ApiModelProperty("助手1配诊时长")
  private Integer treatMatchingTimeAsAssistant1;
  /** 助手1配诊实收工作量 */
  @ApiModelProperty("助手1配诊实收工作量")
  private BigDecimal treatMatchingActualWorkloadAsAssistant1;
  /** 助手1配诊退费工作量 */
  @ApiModelProperty("助手1配诊退费工作量")
  private BigDecimal treatMatchingRefundWorkloadAsAssistant1;
  /** 助手1配诊时长 */
  @ApiModelProperty("助手2配诊时长")
  private Integer treatMatchingTimeAsAssistant2;
  /** 助手1配诊实收工作量 */
  @ApiModelProperty("助手2配诊实收工作量")
  private BigDecimal treatMatchingActualWorkloadAsAssistant2;
  /** 助手1配诊退费工作量 */
  @ApiModelProperty("助手2配诊退费工作量")
  private BigDecimal treatMatchingRefundWorkloadAsAssistant2;
  /** 助手1配诊时长 */
  @ApiModelProperty("巡回配诊时长")
  private Integer treatMatchingTimeAsAssistant3;
  /** 助手1配诊实收工作量 */
  @ApiModelProperty("巡回配诊实收工作量")
  private BigDecimal treatMatchingActualWorkloadAsAssistant3;
  /** 助手1配诊退费工作量 */
  @ApiModelProperty("巡回配诊退费工作量")
  private BigDecimal treatMatchingRefundWorkloadAsAssistant3;
}
