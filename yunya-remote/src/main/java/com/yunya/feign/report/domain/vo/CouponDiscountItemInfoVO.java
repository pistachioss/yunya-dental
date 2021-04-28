package com.yunya.feign.report.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import static com.yunya.framework.common.annation.Excel.ColumnType.NUMERIC;
import static com.yunya.framework.common.annation.Excel.Type.EXPORT;

/**
 * @program: chenl
 * @description: 查看产品优惠项目明细列表数据VO
 * @author: LHB
 * @create: 2020-12-29 10:51
 */
@Data
@ToString
@ApiModel(value = "CouponDiscountItemInfoVO", description = "查看产品优惠项目明细列表数据VO")
public class CouponDiscountItemInfoVO implements Serializable {
  @ApiModelProperty("项目编号")
  @Excel(name = "项目编号")
  private String itemNum;

  @ApiModelProperty("项目名称")
  @Excel(name = "项目名称")
  private String itemName;

  @ApiModelProperty("项目分类")
  @Excel(name = "项目分类")
  private String categoryName;

  @ApiModelProperty("单价")
  @Excel(name = "单价")
  private BigDecimal price;

  @ApiModelProperty("数量")
  @Excel(name = "数量", type = EXPORT, cellType = NUMERIC, isStatistics = true)
  private Integer quantity;

  @ApiModelProperty("原价合计")
  @Excel(name = "原价合计", type = EXPORT, cellType = NUMERIC, isStatistics = true)
  private BigDecimal originPrice;

  @ApiModelProperty("优惠金额")
  @Excel(name = "优惠金额", type = EXPORT, cellType = NUMERIC, isStatistics = true)
  private BigDecimal benefitAmount;

  @ApiModelProperty("账单编号")
  @Excel(name = "账单编号")
  private String billNum;

  @ApiModelProperty("账单日期")
  @Excel(name = "账单日期", dateFormat = "yyyy-MM-dd")
  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
  private Date billDate;

  @ApiModelProperty("患者")
  @Excel(name = "患者")
  private String patientName;

  @ApiModelProperty("手机号")
  @Excel(name = "手机号")
  private String patientMobile;

  @ApiModelProperty("执行人")
  @Excel(name = "执行人")
  private String executor;

  @ApiModelProperty("使用产品")
  @Excel(name = "使用产品")
  private String couponName;

  @ApiModelProperty("产品类型")
  @Excel(name = "产品类型")
  private String couponType;

  @ApiModelProperty("卡号")
  @Excel(name = "卡号")
  private String cardNumber;

  @ApiModelProperty("销售渠道")
  @Excel(name = "销售渠道")
  private String saleChannelName;
}
