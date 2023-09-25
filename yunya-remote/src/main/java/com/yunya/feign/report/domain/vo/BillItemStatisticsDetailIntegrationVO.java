package com.yunya.feign.report.domain.vo;

import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

import static com.yunya.framework.common.annation.Excel.ColumnType.NUMERIC;
import static com.yunya.framework.common.annation.Excel.Type.EXPORT;

/**
 * 简介: 开单数量及金额统计明细一体VO
 *
 * @author: chow
 * @date: 2020/12/7 14:26
 * @description:
 * @since: 1.0.0
 */
@ApiModel("开单数量及金额统计明细一体VO")
@Data
@ToString
public class BillItemStatisticsDetailIntegrationVO implements Serializable {
  /** 门诊 */
  @Excel(name="门诊")
  @ApiModelProperty("门诊")
  private String abbreviation;
  /** 账单日期 */
  @Excel(name = "账单日期")
  @ApiModelProperty("账单日期")
  private String billDate;
  /** 账单编号 */
  @Excel(name = "账单编号")
  @ApiModelProperty("账单编号")
  private String billNum;
  /** 患者ID */
  @ApiModelProperty("患者ID")
  private Integer patientId;
  /** 执行人 */
  @Excel(name = "执行人")
  @ApiModelProperty("执行人")
  private String executorName;
  /** 所属分类 */
  @Excel(name = "所属分类")
  @ApiModelProperty("所属分类")
  private String categoryName;
  /** 项目名称 */
  @Excel(name = "项目名称")
  @ApiModelProperty("项目名称")
  private String itemName;
  /** 开单数量 */
  @Excel(name = "开单数量", cellType = NUMERIC, isStatistics = true, type = EXPORT)
  @ApiModelProperty("开单数量")
  private Integer quantity;
    @Excel(name = "收费数量", cellType = NUMERIC, isStatistics = true, type = EXPORT)
    @ApiModelProperty("收费数量")
    private Integer billQuantity;
  /** 患者姓名 */
  @Excel(name = "患者姓名")
  @ApiModelProperty("患者姓名")
  private String patientName;
  /** 开单备注 */
  @Excel(name = "开单备注")
  @ApiModelProperty("开单备注")
  private String remark;

}
