package com.yunya.feign.report.domain.vo;

import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

import static com.yunya.framework.common.annation.Excel.ColumnType.NUMERIC;
import static com.yunya.framework.common.annation.Excel.Type.EXPORT;

/**
 * 简介: 报表预付款退费vo
 *
 * @author: WY
 * @date: 2020/10/24 14:11
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("预付款退费Vo")
public class BasePrepaidReturnLogVo {

  /** 操作id * */
  @ApiModelProperty("操作id")
  private Integer occurLogId;

  /** 退款日期 */
  @Excel(name = "退费日期")
  @ApiModelProperty("退费日期")
  private String occurDate;

  /** 患者 */
  @Excel(name = "患者")
  @ApiModelProperty("患者")
  private String name;

  /** 手机号 */
  @Excel(name = "手机号")
  @ApiModelProperty("手机号")
  private String mobile;

  /** 预付款账号 */
  @Excel(name = "预付款账号")
  @ApiModelProperty("预付款账号")
  private String cardNumber;

  /** 退本金金额 */
  @Excel(name = "退本金金额", cellType = NUMERIC, type = EXPORT, isStatistics = true)
  @ApiModelProperty("退本金金额")
  private BigDecimal principalAmount = new BigDecimal(0);

  /** 退赠金金额 */
  @Excel(name = "退赠金金额", cellType = NUMERIC, type = EXPORT, isStatistics = true)
  @ApiModelProperty("退赠金金额")
  private BigDecimal bonusAmount = new BigDecimal(0);

  /** 退费后预付款余额（含赠送金额） */
  @Excel(name = "退费后预付款余额（含赠送金额）", cellType = NUMERIC, type = EXPORT, isStatistics = true)
  @ApiModelProperty("退费后预付款余额（含赠送金额）")
  private BigDecimal currentReturnPrincipal = new BigDecimal(0);

  /** 剩余预付款余额（含赠送金额） */
  @Excel(name = "剩余预付款余额（含赠送金额）", cellType = NUMERIC, type = EXPORT, isStatistics = true)
  @ApiModelProperty("剩余预付款余额（含赠送金额）")
  private BigDecimal currentAmount = new BigDecimal(0);

  /** 入账方式字典id */
  @ApiModelProperty("入账方式字典id")
  private Integer paymentId;

  /** 退费方式 */
  @Excel(name = "退费方式")
  @ApiModelProperty("退费方式")
  private String paymentManner;

  /** 退费人 */
  @Excel(name = "退费人")
  @ApiModelProperty("退费人")
  private String operatorUserName;

  /** 退费原因 */
  @Excel(name = "退费原因")
  @ApiModelProperty("退费原因")
  private String remarks;
}
