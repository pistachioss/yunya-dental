package com.yunya.feign.report.domain.vo;

import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

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
  /** 已收工作量 */
  @Excel(name = "已收工作量", scale = 2, cellType = Excel.ColumnType.NUMERIC, isStatistics = true)
  @ApiModelProperty("已收工作量")
  private BigDecimal receivedWorkload;
  /** 免单工作量 */
  @Excel(name = "其中免单工作量", scale = 2, cellType = Excel.ColumnType.NUMERIC, isStatistics = true)
  @ApiModelProperty("免单工作量")
  private BigDecimal freePayWorkload;
  /** 补入工作量 */
  @Excel(name = "补入工作量", scale = 2, cellType = Excel.ColumnType.NUMERIC, isStatistics = true)
  @ApiModelProperty("补入工作量")
  private BigDecimal supplyWorkload;
  /** 退费工作量 */
  @Excel(name = "退费工作量", scale = 2, cellType = Excel.ColumnType.NUMERIC, isStatistics = true)
  @ApiModelProperty("退费工作量")
  private BigDecimal refundWorkload;
}
