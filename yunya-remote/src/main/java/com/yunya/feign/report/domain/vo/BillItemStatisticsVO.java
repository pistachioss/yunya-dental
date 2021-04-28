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
 * 简介: 开单项目数量及金额VO
 *
 * @author: chow
 * @date: 2020/12/7 10:40
 * @description:
 * @since: 1.0.0
 */
@ApiModel("开单项目数量及金额VO")
@Data
@ToString
public class BillItemStatisticsVO implements Serializable {
  /** 项目ID */
  @ApiModelProperty("项目ID")
  private Integer itemId;

  /** 项目类型 */
  @ApiModelProperty("项目类型：0-价目表；1-商品")
  private Byte itemType;

  /** 门诊ID */
  @ApiModelProperty("门诊ID")
  private Integer orgId;

  /** 门诊 */
  @Excel(name = "门诊")
  @ApiModelProperty("门诊")
  private String abbreviation;

  /** 项目编号 */
  @Excel(name = "项目编号")
  @ApiModelProperty("项目编号")
  private String itemNum;

  @ApiModelProperty("执行人ID")
  private String employeeId;

  @Excel(name = "执行人")
  @ApiModelProperty("执行人")
  private String employeeName;

  /** 项目分类名称 */
  @Excel(name = "所属分类")
  @ApiModelProperty("所属分类")
  private String categoryName;

  /** 项目名称 */
  @Excel(name = "项目名称")
  @ApiModelProperty("项目名称")
  private String itemName;

  /** 开单数量 */
  @Excel(name = "数量", cellType = NUMERIC, type = EXPORT, isStatistics = true)
  @ApiModelProperty("开单数量")
  private Integer quantity;

  /** 实收金额 */
  @Excel(name = "实收金额", cellType = NUMERIC, type = EXPORT, isStatistics = true)
  @ApiModelProperty("实收金额")
  private BigDecimal amount;
}
