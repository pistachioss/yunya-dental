package com.yunya.feign.report.domain.vo;

import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介: 项目分类收入汇总信息
 *
 * @author: chow
 * @date: 2020/11/24 20:45
 * @description:
 * @since: 1.0.0
 */
@ApiModel("项目分类收入汇总信息")
@Data
@ToString
public class CategoryInfoIncomeVO implements Serializable {
  /** 项目分类ID */
  @ApiModelProperty("项目分类ID")
  private Integer categoryId;
  /** 项目分类类型：0-价目表；1-商品 */
  //  @Excel(name = "项目类型", readConverterExp = "0=开单处置,1-商品")
  @ApiModelProperty("项目分类类型：0-价目表；1-商品")
  private Byte categoryType;
  /** 项目分类名称 */
  @Excel(name = "项目分类名称")
  @ApiModelProperty("项目分类名称")
  private String categoryName;
  /** 原价合计 */
  @Excel(name = "优惠金额合计", scale = 2, cellType = Excel.ColumnType.NUMERIC)
  @ApiModelProperty("优惠金额合计")
  private BigDecimal totalOriginalAmount;
  /** 实收金额合计 */
  @Excel(name = "实收金额合计", scale = 2, cellType = Excel.ColumnType.NUMERIC)
  @ApiModelProperty("实收金额合计")
  private BigDecimal totalActualAmount;
  /** 优惠金额合计 */
  @Excel(name = "优惠金额合计", scale = 2, cellType = Excel.ColumnType.NUMERIC)
  @ApiModelProperty("优惠金额合计")
  private BigDecimal totalDiscountAmount;
  /** 当月免单金额*/
  @Excel(name = "当月免单金额", scale = 2, cellType = Excel.ColumnType.NUMERIC)
  @ApiModelProperty("当月免单金额")
  private BigDecimal totalFreePaymentAmount;
  /** 项目补入工作量合计 */
  @Excel(name = "项目补入工作量合计", scale = 2, cellType = Excel.ColumnType.NUMERIC)
  @ApiModelProperty("项目补入工作量合计")
  private BigDecimal totalCouponAmount;
  /** 合计收入 = 实收金额合计-当月免单金额+项目补入工作量合计 */
  @Excel(name = "合计收入", scale = 2, cellType = Excel.ColumnType.NUMERIC)
  @ApiModelProperty("合计收入")
  private BigDecimal totalAmount;
}
