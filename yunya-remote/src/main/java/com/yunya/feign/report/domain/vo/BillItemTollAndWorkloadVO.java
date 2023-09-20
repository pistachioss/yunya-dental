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
 * 简介: 项目收费及工作量VO
 *
 * @author: chow
 * @date: 2021/4/1 11:08
 * @description:
 * @since: 1.0.0
 */
@ApiModel("项目收费及工作量VO")
@Data
@ToString
public class BillItemTollAndWorkloadVO implements Serializable {
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

  @Excel(name = "收费数量", cellType = NUMERIC, isStatistics = true, type = EXPORT)
  @ApiModelProperty("收费数量")
  private Integer billQuantity = 0;

  /** 实收工作量 */
  @Excel(name = "实收工作量",  cellType = NUMERIC, isStatistics = true, type = EXPORT)
  @ApiModelProperty("实收工作量")
  private BigDecimal receivedWorkload = BigDecimal.ZERO;
  /** 免单工作量 */
  @Excel(name = "其中免单工作量",  cellType = NUMERIC, isStatistics = true, type = EXPORT)
  @ApiModelProperty("免单工作量")
  private BigDecimal freePayWorkload = BigDecimal.ZERO;
  /** 补入工作量 */
  @Excel(name = "补入工作量",  cellType = NUMERIC, isStatistics = true, type = EXPORT)
  @ApiModelProperty("补入工作量")
  private BigDecimal supplyWorkload = BigDecimal.ZERO;
  /** 退费工作量 */
  @Excel(name = "退费工作量",  cellType = NUMERIC, isStatistics = true, type = EXPORT)
  @ApiModelProperty("退费工作量")
  private BigDecimal refundWorkload = BigDecimal.ZERO;
}
