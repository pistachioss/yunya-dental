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
 * 简介: 报表预付款充值vo
 *
 * @author: WY
 * @date: 2020/10/24 14:11
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("预付款充值列表Vo")
public class BasePrepaidRechargeLogVo {

  /** 操作id * */
  @ApiModelProperty("操作id")
  private Integer occurLogId;

  /** 充值日期 */
  @Excel(name = "充值日期")
  @ApiModelProperty("充值日期")
  private String occurDate;

  /** 患者姓名 */
  @Excel(name = "患者姓名")
  @ApiModelProperty("患者姓名")
  private String name;

  /** 手机号 */
  @Excel(name = "手机号")
  @ApiModelProperty("手机号")
  private String mobile;

  /** 预付款类型 */
  @Excel(name = "预付款类型")
  @ApiModelProperty("预付款类型(１：预付款（普通），2-正畸预付款，3-美白预付款)")
  private String type;

  /** 预付款账号 */
  @Excel(name = "预付款账号")
  @ApiModelProperty("预付款账号")
  private String cardNumber;

  /** 充值本金金额 */
  @Excel(name = "充值本金金额", cellType = NUMERIC, isStatistics = true, type = EXPORT)
  @ApiModelProperty("充值本金金额")
  private BigDecimal principalAmount = new BigDecimal(0);

  /** 充值赠金金额 */
  @Excel(name = "充值赠金金额", cellType = NUMERIC, isStatistics = true, type = EXPORT)
  @ApiModelProperty("充值赠金金额")
  private BigDecimal bonusAmount = new BigDecimal(0);

  /** 充值后预付款余额（含赠送金额） */
  @Excel(name = "充值后预付款余额（含赠送金额）", cellType = NUMERIC, isStatistics = true, type = EXPORT)
  @ApiModelProperty("充值后预付款余额（含赠送金额）")
  private BigDecimal currentRechargePrincipal = new BigDecimal(0);

  /** 剩余预付款余额（含赠送金额） */
  @Excel(name = "剩余预付款余额（含赠送金额）", cellType = NUMERIC, isStatistics = true, type = EXPORT)
  @ApiModelProperty("剩余预付款余额（含赠送金额）")
  private BigDecimal currentAmount = new BigDecimal(0);

  /** 充值方式 */
  @Excel(name = "充值方式 ")
  @ApiModelProperty("充值方式")
  private String rechargeMethod;

  /** 入账方式字典id */
  @ApiModelProperty("入账方式字典id")
  private Integer paymentId;

  /** 入账方式 */
  @Excel(name = "入账方式")
  @ApiModelProperty("入账方式")
  private String paymentManner;

  /** 充值卡号 */
  @Excel(name = "充值卡号")
  @ApiModelProperty("充值卡号")
  private String rechargeCardNumber;

  /** 充值人 */
  @Excel(name = "充值人")
  @ApiModelProperty("充值人")
  private String operatorUserName;

  /** 备注 */
  @Excel(name = "备注")
  @ApiModelProperty("备注")
  private String remarks;
}
