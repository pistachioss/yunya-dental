package com.yunya.feign.report.domain.vo;

import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介: 账单项目收入详情VO
 *
 * @author: chow
 * @date: 2020/10/29 11:23
 * @description:
 * @since: 1.0.0
 */
@ApiModel("账单项目收入详情VO")
@Data
@ToString
public class BillTariffIncomeDetailVO implements Serializable {
  /** 开单明细ID */
  @ApiModelProperty("账单明细ID")
  private Integer billDetailId;
  /** 项目ID */
  @ApiModelProperty("项目ID")
  private Integer itemId;
  /** 项目编号 */
  @Excel(name = "项目编号")
  @ApiModelProperty("项目编号")
  private String itemNum;
  /** 项目名称 */
  @Excel(name = "项目名称")
  @ApiModelProperty("项目名称")
  private String itemName;
  /** 项目类型(0-价目表；1-商品) */
  @ApiModelProperty("项目类型(0-价目表；1-商品)")
  private Byte itemType;
  /** 分类ID */
  @ApiModelProperty("项目分类ID")
  private Integer categoryId;
  /** 分类名称 */
  @Excel(name = "项目分类")
  @ApiModelProperty("项目分类名称")
  private String categoryName;
  /** 账单编号 */
  @Excel(name = "账单编号")
  @ApiModelProperty("账单编号")
  private String billNum;
  /** 开单日期 */
  @Excel(name = "开单日期")
  @ApiModelProperty("开单日期")
  private String orderDate;
  /** 患者ID */
  @ApiModelProperty("患者ID")
  private Integer patientId;
  /** 患者姓名 */
  @Excel(name = "患者")
  @ApiModelProperty("患者姓名")
  private String patientName;
  /** 患者手机号 */
  @Excel(name = "手机号")
  @ApiModelProperty("患者手机号")
  private String mobile;
  /** 患者性别 */
  @ApiModelProperty("患者性别")
  private Byte gender;
  /** 执行人ID */
  @ApiModelProperty("执行人ID")
  private Integer executorId;
  /** 执行人姓名 */
  @Excel(name = "执行人")
  @ApiModelProperty(name = "执行人姓名")
  private String executorName;
  /** 单价 */
  @Excel(name = "单价")
  @ApiModelProperty("单价")
  private BigDecimal price;
  /** 数量 */
  @Excel(name = "数量")
  @ApiModelProperty("数量")
  private Integer quantity;
  /** 原价合计 */
  @Excel(name = "原价")
  @ApiModelProperty("原价合计(单价*数量)")
  private BigDecimal totalOriginalAmount;
  /** 优惠金额 */
  @Excel(name = "优惠金额")
  @ApiModelProperty("优惠金额")
  private BigDecimal discountAmount;
  /** 实收金额 */
  @Excel(name = "实收金额")
  @ApiModelProperty("实收金额（原价合计-折扣金额）")
  private BigDecimal actualAmount;
}
