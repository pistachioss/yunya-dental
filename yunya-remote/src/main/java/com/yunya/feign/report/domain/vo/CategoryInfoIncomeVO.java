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

  /** 组织ID */
  @ApiModelProperty("组织ID")
  private Integer orgId;
  /** 组织简称 */
  @Excel(name = "门诊")
  @ApiModelProperty("组织简称")
  private String abbreviation;
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
  @Excel(name = "原价合计", cellType = NUMERIC, type = EXPORT, isStatistics = true)
  @ApiModelProperty("原价合计")
  private BigDecimal totalOriginalAmount = BigDecimal.ZERO;
  /** 优惠金额合计 */
  @Excel(name = "优惠金额合计", cellType = NUMERIC, type = EXPORT, isStatistics = true)
  @ApiModelProperty("优惠金额合计")
  private BigDecimal totalDiscountAmount = BigDecimal.ZERO;
  /** 应收金额合计 */
  @Excel(name = "应收金额合计", cellType = NUMERIC, type = EXPORT, isStatistics = true)
  @ApiModelProperty("应收金额合计")
  private BigDecimal totalActualAmount = BigDecimal.ZERO;
  /** 当月免单金额*/
  @Excel(name = "当月免单金额", cellType = NUMERIC, type = EXPORT, isStatistics = true)
  @ApiModelProperty("当月免单金额")
  private BigDecimal totalFreePaymentAmount = BigDecimal.ZERO;
  /** 项目补入工作量合计 */
  @Excel(name = "项目补入工作量合计", cellType = NUMERIC, type = EXPORT, isStatistics = true)
  @ApiModelProperty("项目补入工作量合计")
  private BigDecimal totalCouponAmount = BigDecimal.ZERO;
  /** 合计收入 = 应收金额合计-当月免单金额+项目补入工作量合计 */
  @Excel(name = "合计收入", cellType = NUMERIC, type = EXPORT, isStatistics = true)
  @ApiModelProperty("合计收入")
  private BigDecimal totalAmount = BigDecimal.ZERO;
    @Excel(name = "划扣金额合计", cellType = NUMERIC, type = EXPORT, isStatistics = true)
    @ApiModelProperty("划扣金额合计")
    private BigDecimal totalDeductionAmount = BigDecimal.ZERO;
    @Excel(name = "划扣补入工作量合计", cellType = NUMERIC, type = EXPORT, isStatistics = true)
    @ApiModelProperty("划扣补入工作量合计")
    private BigDecimal totalDeductionWorkload = BigDecimal.ZERO;
}
