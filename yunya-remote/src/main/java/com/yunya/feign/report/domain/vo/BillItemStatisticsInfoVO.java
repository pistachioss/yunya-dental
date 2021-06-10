package com.yunya.feign.report.domain.vo;

import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

import static com.yunya.framework.common.annation.Excel.ColumnType.NUMERIC;
import static com.yunya.framework.common.annation.Excel.Type.EXPORT;

/**
 * 简介: 个人开单项目实收金额及数量VO
 *
 * @author: chenl
 * @date: 2021/05/17 14:26
 * @description:
 * @since: 1.0.0
 */
@ApiModel("个人开单项目实收金额及数量VO")
@Data
@ToString
public class BillItemStatisticsInfoVO implements Serializable {
  /** 门诊 */
  @Excel(name = "门诊")
  @ApiModelProperty("门诊")
  private String abbreviation;
  /** 账单日期 */
  @Excel(name = "账单日期")
  @ApiModelProperty("账单日期")
  private String billDate;
  /** 项目编号 */
  @Excel(name = "项目编号")
  @ApiModelProperty("项目编号")
  private String itemNum;
  /** 挂号医生 */
  @Excel(name = "挂号医生")
  @ApiModelProperty("挂号医生")
  private String regDentistName;
  /** 项目分类名称 */
  @Excel(name = "所属分类")
  @ApiModelProperty("项目分类名称")
  private String categoryName;
  /** 项目名称 */
  @Excel(name = "项目名称")
  @ApiModelProperty("项目名称")
  private String itemName;
  /** 开单数量 */
  @Excel(name = "开单数量", cellType = NUMERIC, isStatistics = true, type = EXPORT)
  @ApiModelProperty("开单数量")
  private Integer quantity;
  /** 应收金额 */
  @Excel(name = "应收金额",  cellType = NUMERIC, isStatistics = true, type = EXPORT)
  @ApiModelProperty("应收金额")
  private BigDecimal amount;
  /** 执行人 */
  @Excel(name = "执行人")
  @ApiModelProperty("执行人")
  private String executorName;
  /** 账单编号 */
  @Excel(name = "账单编号")
  @ApiModelProperty("账单编号")
  private String billNum;
  /** 患者姓名 */
  @Excel(name = "患者姓名")
  @ApiModelProperty("患者姓名")
  private String patientName;
  /** 患者ID */
  @ApiModelProperty("患者ID")
  private Integer patientId;
  /** 手机号 */
  @Excel(name = "手机号")
  @ApiModelProperty("手机号")
  private String mobile;
}
