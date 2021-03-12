package com.yunya.feign.report.domain.vo;

import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介: 运营综合数据VO
 *
 * @author: chow
 * @date: 2021/1/18 16:21
 * @description:
 * @since: 1.0.0
 */
@ApiModel("运营综合数据VO（工作量完成率、初诊人数目标完成率、老患者介绍率、随访完成率、提醒完成率）")
@Data
@ToString
public class OperationDataBusinessGoalVO implements Serializable {
  /**
   * 门诊id
   */
  @ApiModelProperty("门诊id")
  private Integer orgId;

  /** 门诊 */
  @Excel(name = "门诊")
  @ApiModelProperty("门诊")
  private String abbreviation;

  /** 工作量目标金额/完成金额/完成率 */
  @Excel(name = "工作量目标金额/完成金额/完成率")
  @ApiModelProperty("工作量目标金额/完成金额/完成率")
  private String workload;

  /** 初诊目标人数/完成人数/完成率 */
  @Excel(name = "初诊目标人数/完成金额/完成率")
  @ApiModelProperty("初诊目标人数/完成人数/完成率")
  private String firstVisit;

  /** 初诊总人数/老患者介绍人数/老患者介绍率 */
  @Excel(name = "初诊总人数/老患者介绍人数/老患者介绍率")
  @ApiModelProperty("初诊总人数/老患者介绍人数/老患者介绍率")
  private String introduction;

  /** 随访总数/完成随访数/随访完成率 */
  @Excel(name = "随访总数/完成随访数/随访完成率")
  @ApiModelProperty("随访总数/完成随访数/随访完成率")
  private String followUp;

  @ApiModelProperty("随访总数")
  private Integer followUpTotal;

  /** 随访总数/完成随访数/随访完成率 */
  @Excel(name = "随访总数/完成随访数/随访完成率")
  @ApiModelProperty("随访总数/完成随访数/随访完成率")
  private String notice;

  @ApiModelProperty("提醒总数")
  private Integer noticeTotal;
}
