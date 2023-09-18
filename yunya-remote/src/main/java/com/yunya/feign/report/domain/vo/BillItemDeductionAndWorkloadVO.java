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
@ApiModel("划扣项目收费及工作量VO")
@Data
@ToString
public class BillItemDeductionAndWorkloadVO implements Serializable {
  /** 门诊ID */
  @ApiModelProperty("门诊ID")
  private Integer orgId;
  /** 门诊名称 */
  @Excel(name = "门诊名称")
  @ApiModelProperty("门诊名称")
  private String abbreviation;
  /** 项目分类ID */
  @ApiModelProperty("项目分类ID")
  private Integer itemCategoryId;
  /** 项目明细ID */
  @ApiModelProperty("项目明细ID")
  private Integer itemId;
  /** 项目明细编号 */
  @Excel(name = "项目编号")
  @ApiModelProperty("项目明细编号")
  private String itemNum;
  /** 执行人ID */
  @ApiModelProperty("执行人ID")
  private Integer executorId;
  /** 执行人姓名 */
  @Excel(name = "执行人")
  @ApiModelProperty("执行人姓名")
  private String executorName;
  /** 项目分类名称 */
  @Excel(name = "所属分类")
  @ApiModelProperty("项目分类名称")
  private String itemCategoryName;
  /** 项目明细名称 */
  @Excel(name = "项目名称")
  @ApiModelProperty("项目明细名称")
  private String itemName;

  /** 开单数量 */
  @Excel(name = "开单数量", cellType = NUMERIC, isStatistics = true, type = EXPORT)
  @ApiModelProperty("开单数量")
  private Integer quantity = 0;

  @Excel(name = "划扣数量", cellType = NUMERIC, isStatistics = true, type = EXPORT)
  @ApiModelProperty("划扣数量")
  private Integer deductionQuantity = 0;

  /** 补入工作量 */
  @Excel(name = "划扣工作量",  cellType = NUMERIC, isStatistics = true, type = EXPORT)
  @ApiModelProperty("划扣工作量")
  private BigDecimal deductionWorkload = BigDecimal.ZERO;
  /** 退费工作量 */
  @Excel(name = "划扣补入工作量",  cellType = NUMERIC, isStatistics = true, type = EXPORT)
  @ApiModelProperty("划扣补入工作量")
  private BigDecimal deductionCouponWorkload = BigDecimal.ZERO;
}
