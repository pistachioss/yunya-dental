package com.yunya.feign.report.domain.vo;

import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import static com.yunya.framework.common.annation.Excel.ColumnType.NUMERIC;
import static com.yunya.framework.common.annation.Excel.Type.EXPORT;

/**
 * 简介: 患者维度统计VO
 *
 * @author: chenl
 * @date: 2021/12/01 14:26
 * @description:
 * @since: 1.0.0
 */
@ApiModel("患者维度统计VO")
@Data
@ToString
public class PatientDimensionStatisticsVO implements Serializable {
  /** 患者姓名 */
  @Excel(name = "患者姓名")
  @ApiModelProperty("患者姓名")
  private String patientName;
  /** 患者年龄 */
  @Excel(name = "年龄")
  @ApiModelProperty("年龄")
  private Integer age;
  /** 患者来源 */
  @Excel(name = "患者来源")
  @ApiModelProperty("患者来源")
  private String originSource;
  /** 会员等级 */
  @Excel(name = "会员等级")
  @ApiModelProperty("会员等级")
  private String memberLevel;
  /** 就诊次数 */
  @Excel(name = "就诊次数", cellType = NUMERIC, isStatistics = true, type = EXPORT)
  @ApiModelProperty("就诊次数")
  private Integer treatNum;
  /** 累计消费 */
  @Excel(name = "累计消费", cellType = NUMERIC, isStatistics = true, type = EXPORT)
  @ApiModelProperty("累计消费")
  private BigDecimal totalReceivedAmount;
  /** 欠费总额 */
  @Excel(name = "欠费总额", cellType = NUMERIC, isStatistics = true, type = EXPORT)
  @ApiModelProperty("欠费总额")
  private BigDecimal totalDebtAmount;
  /** 初诊日期 */
  @Excel(name = "初诊日期")
  @ApiModelProperty("初诊日期")
  private Date firstVisitDate;
  /** 末次就诊日期 */
  @Excel(name = "末次就诊日期")
  @ApiModelProperty("末次就诊日期")
  private Date lasteastVisitDate;
  /** 下次预约 */
  @Excel(name = "下次预约")
  @ApiModelProperty("下次预约")
  private Date nextAppointDate;
  /** 下次提醒 */
  @Excel(name = "下次提醒")
  @ApiModelProperty("下次提醒")
  private Date nextRemindDate;
  /** 专科数量 */
  @Excel(name = "专科数量", cellType = NUMERIC, isStatistics = true, type = EXPORT)
  @ApiModelProperty("专科数量")
  private Integer specialProjectNum;
}
