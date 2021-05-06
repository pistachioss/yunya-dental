package com.yunya.feign.patient_central.domain.vo.web;

import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

import static com.yunya.framework.common.annation.Excel.ColumnType.NUMERIC;
import static com.yunya.framework.common.annation.Excel.Type.EXPORT;

/**
 * 简介:
 *
 * @author: WY
 * @date: 2021/3/19 17:28
 * @description: 活动推荐列表结果返回模板
 * @since: 1.0.0
 */
@ApiModel(value = "activityInfoVo", description = "活动推荐列表结果返回模板")
@Data
public class PatientOriginActivityVo {

  /** 活动id */
  @ApiModelProperty(value = "活动id", required = false)
  private Integer originId;

  /** 活动名称 */
  @Excel(name = "活动名称")
  @ApiModelProperty(value = "活动名称", required = false)
  private String activityName;

  /** 患者数量 */
  @Excel(name = "患者数量", cellType = NUMERIC, isStatistics = true, type = EXPORT)
  @ApiModelProperty(value = "患者数量", required = false)
  private String patientNumber;

  /** 实收工作量合计 */
  @Excel(name = "实收工作量合计", cellType = NUMERIC, isStatistics = true, type = EXPORT)
  @ApiModelProperty(value = "实收工作量合计", required = false)
  private BigDecimal receivedTotalWorkload = new BigDecimal(0);

  /** 其中免单支付工作量合计 */
  @Excel(name = "其中免单支付工作量合计", cellType = NUMERIC, isStatistics = true, type = EXPORT)
  @ApiModelProperty(value = "其中免单支付工作量合计", required = false)
  private BigDecimal freeTotalWorkload = new BigDecimal(0);

  /** 退费金额合计 */
  @Excel(name = "退费金额合计", cellType = NUMERIC, isStatistics = true, type = EXPORT)
  @ApiModelProperty(value = "退费金额合计", required = false)
  private BigDecimal totalRefundAmount = new BigDecimal(0);

  /** 补入工作量合计 */
  @Excel(name = "补入工作量合计", cellType = NUMERIC, isStatistics = true, type = EXPORT)
  @ApiModelProperty(value = "补入工作量合计", required = false)
  private BigDecimal makeUpWorkload = new BigDecimal(0);
}
