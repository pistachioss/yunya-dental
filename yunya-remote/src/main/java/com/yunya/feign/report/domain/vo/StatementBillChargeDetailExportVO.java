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
 * 简介: 对账单账单收费明细导出VO
 *
 * @author: chow
 * @date: 2021/1/13 13:17
 * @description:
 * @since: 1.0.0
 */
@ApiModel("对账单账单收费明细导出VO")
@Data
@ToString
public class StatementBillChargeDetailExportVO implements Serializable {
  /** 收费日期 */
  @Excel(name = "收费日期")
  @ApiModelProperty("收费（收欠费）日期")
  private String payeeDate;
  /** 收费诊所名称 */
  @Excel(name = "代收门诊")
  @ApiModelProperty("收费门诊")
  private String payeeOrgName;
  /** 账单编号 */
  @Excel(name = "账单编号")
  @ApiModelProperty("账单编号")
  private String billNum;
  /** 开单门诊名称 */
  @Excel(name = "开单门诊")
  @ApiModelProperty("开单门诊")
  private String billOrgName;
  /** 账单日期 */
  @Excel(name = "账单日期")
  @ApiModelProperty("账单日期")
  private String billDate;
  /** 患者姓名 */
  @Excel(name = "患者")
  @ApiModelProperty("患者姓名")
  private String patientName;
  /** 手机号 */
  @Excel(name = "手机号")
  @ApiModelProperty("手机号")
  private String patientMobile;
  /** 挂号医生 */
  @Excel(name = "挂号医生")
  @ApiModelProperty("挂号医生")
  private String regDentistName;
  /** 原价合计 */
  @Excel(name = "原价合计", cellType = NUMERIC, type = EXPORT, isStatistics = true)
  @ApiModelProperty("原价合计")
  private BigDecimal originalAmount;
  /** 优惠金额 */
  @Excel(name = "优惠金额", cellType = NUMERIC, type = EXPORT, isStatistics = true)
  @ApiModelProperty("优惠金额")
  private BigDecimal privilegeAmount;
  /** 应收金额 */
  @Excel(name = "应收金额", cellType = NUMERIC, type = EXPORT, isStatistics = true)
  @ApiModelProperty("应收金额")
  private BigDecimal actualAmount;
  /** 实收金额 */
  @Excel(name = "实收金额", cellType = NUMERIC, type = EXPORT, isStatistics = true)
  @ApiModelProperty("实收金额")
  private BigDecimal totalReceivedAmount;
  /** 本次收费金额 */
  @Excel(name = "本次收费金额", cellType = NUMERIC, type = EXPORT, isStatistics = true)
  @ApiModelProperty("本次收费金额")
  private BigDecimal receivedAmount;
  /** 会员卡本金 */
  @Excel(name = "会员卡本金", cellType = NUMERIC, type = EXPORT, isStatistics = true)
  @ApiModelProperty("会员卡本金")
  private BigDecimal memberPrincipleAmount;
  /** 会员卡赠金 */
  @Excel(name = "会员卡赠金", cellType = NUMERIC, type = EXPORT, isStatistics = true)
  @ApiModelProperty("会员卡赠金")
  private BigDecimal memberBonusAmount;
  /** 预付款本金 */
  @Excel(name = "预付款本金", cellType = NUMERIC, type = EXPORT, isStatistics = true)
  @ApiModelProperty("预付款本金")
  private BigDecimal prepaidPrincipleAmount;
  /** 预付款赠金 */
  @Excel(name = "预付款赠金", cellType = NUMERIC, type = EXPORT, isStatistics = true)
  @ApiModelProperty("预付款赠金")
  private BigDecimal prepaidBonusAmount;
  /** 现金 */
  @Excel(name = "现金", cellType = NUMERIC, type = EXPORT, isStatistics = true)
  @ApiModelProperty("现金")
  private BigDecimal cashAmount;
  /** 支付宝 */
  @Excel(name = "支付宝", cellType = NUMERIC, type = EXPORT, isStatistics = true)
  @ApiModelProperty("支付宝")
  private BigDecimal aliPayAmount;
  /** 微信 */
  @Excel(name = "微信", cellType = NUMERIC, type = EXPORT, isStatistics = true)
  @ApiModelProperty("微信")
  private BigDecimal weChatAmount;
  /** 银行账户 */
  @Excel(name = "银行账户", cellType = NUMERIC, type = EXPORT, isStatistics = true)
  @ApiModelProperty("银行账户")
  private BigDecimal bankAmount;
  /** 浙江省医保 */
  @Excel(name = "浙江省医保", cellType = NUMERIC, type = EXPORT, isStatistics = true)
  @ApiModelProperty("浙江省医保")
  private BigDecimal zheJiangProvinceMedicalInsuranceAmount;
  /** 杭州市医保 */
  @Excel(name = "杭州市医保", cellType = NUMERIC, type = EXPORT, isStatistics = true)
  @ApiModelProperty("杭州市医保")
  private BigDecimal hangZhouCityMedicalInsuranceAmount;
  /** 杭州市余杭医保 */
  @Excel(name = "杭州市余杭医保", cellType = NUMERIC, type = EXPORT, isStatistics = true)
  @ApiModelProperty("杭州市余杭医保")
  private BigDecimal hangZhouYuHangMedicalInsuranceAmount;
  /** 杭州市萧山医保 */
  @Excel(name = "杭州市萧山医保", cellType = NUMERIC, type = EXPORT, isStatistics = true)
  @ApiModelProperty("杭州市萧山医保")
  private BigDecimal hangZhouXiaoShanMedicalInsuranceAmount;
  /** 招商信诺 */
  @Excel(name = "招商信诺", cellType = NUMERIC, type = EXPORT, isStatistics = true)
  @ApiModelProperty("招商信诺")
  private BigDecimal zhaoShangXinNuoAmount;
  /** Cigna */
  @Excel(name = "Cigna", cellType = NUMERIC, type = EXPORT, isStatistics = true)
  @ApiModelProperty("Cigna")
  private BigDecimal cignaAmount;
  /** MSH */
  @Excel(name = "MSH", cellType = NUMERIC, type = EXPORT, isStatistics = true)
  @ApiModelProperty("MSH")
  private BigDecimal mshAmount;
  /** AXA */
  @Excel(name = "AXA", cellType = NUMERIC, type = EXPORT, isStatistics = true)
  @ApiModelProperty("AXA")
  private BigDecimal axaAmount;
  /** 风石 */
  @Excel(name = "中意保险", cellType = NUMERIC, type = EXPORT, isStatistics = true)
  @ApiModelProperty("中意保险")
  private BigDecimal fengShiAmount;
  /** 本次免单支付 */
  @Excel(name = "本次免单支付", cellType = NUMERIC, type = EXPORT, isStatistics = true)
  @ApiModelProperty("本次免单支付")
  private BigDecimal thisWaiverAmount;
  /** 艾维员工免单 */
  @Excel(name = "艾维员工免单", cellType = NUMERIC, type = EXPORT, isStatistics = true)
  @ApiModelProperty("艾维员工免单")
  private BigDecimal employeeWaiverAmount;
  /** 2020年会员充值送 */
  @Excel(name = "2020年会员充值送", cellType = NUMERIC, type = EXPORT, isStatistics = true)
  @ApiModelProperty("2020年会员充值送")
  private BigDecimal memberRechargeAmount;
  /** 收费人姓名 */
  @Excel(name = "收费人")
  @ApiModelProperty("收费人姓名")
  private String payeeName;
}
