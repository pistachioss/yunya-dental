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
 * 简介: 门诊当前月账单明细VO
 *
 * @author: chow
 * @date: 2021/1/6 10:43
 * @description:
 * @since: 1.0.0
 */
@ApiModel("门诊当前月账单明细VO")
@Data
@ToString
public class CurrentMonthBillDetailVO implements Serializable {
  /** 账单明细ID */
  @ApiModelProperty("账单明细ID")
  private Integer billDetailId;
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
  /** 病历号 */
  @Excel(name = "病历号")
  @ApiModelProperty("病历号")
  private String medicalNum;
  /** 患者姓名 */
  @Excel(name = "患者姓名")
  @ApiModelProperty("患者姓名")
  private String patientName;
  /** 患者手机号 */
  @ApiModelProperty("患者手机号")
  private String patientMobile;
  /** 项目ID */
  @ApiModelProperty("项目ID")
  private Integer itemId;
  /** 项目类型 */
  @ApiModelProperty("项目类型：0-价目表；1-商品")
  private Byte itemType;
  /** 项目分类ID */
  @ApiModelProperty("项目分类ID")
  private Integer itemCategoryId;
  /** 项目分类名称 */
  @Excel(name = "项目分类")
  @ApiModelProperty("项目分类名称")
  private String itemCategoryName;
  /** 项目名称 */
  @Excel(name = "项目名称")
  @ApiModelProperty("项目名称")
  private String itemName;
  /** 数量 */
  @Excel(name = "数量", cellType = NUMERIC, type = EXPORT, isStatistics = true)
  @ApiModelProperty("数量")
  private Integer quantity;
  /** 项目应收金额（项目原价*数量-该项目的优惠金额） */
  @Excel(name = "项目应收金额", cellType = NUMERIC, type = EXPORT, isStatistics = true)
  @ApiModelProperty("项目应收金额（项目原价*数量-该项目的优惠金额）")
  private BigDecimal itemActualAmount;
  /** 账单应收金额（账单原价合计-账单优惠金额） */
  @Excel(name = "账单应收金额", cellType = NUMERIC, type = EXPORT, isStatistics = true)
  @ApiModelProperty(" 账单应收金额（账单原价合计-账单优惠金额）")
  private BigDecimal billActualAmount;
  /** 项目补入工作量 */
  @Excel(name = "项目补入工作量", cellType = NUMERIC, type = EXPORT, isStatistics = true)
  @ApiModelProperty("项目补入工作量")
  private BigDecimal itemSupplyWorkloadAmount;
}
