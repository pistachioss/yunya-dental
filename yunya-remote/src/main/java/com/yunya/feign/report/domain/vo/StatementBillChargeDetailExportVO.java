package com.yunya.feign.report.domain.vo;

import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

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
  @Excel(name = "原价合计", scale = 2)
  @ApiModelProperty("原价合计")
  private BigDecimal originalAmount;
  /** 优惠金额 */
  @Excel(name = "优惠金额", scale = 2)
  @ApiModelProperty("优惠金额")
  private BigDecimal privilegeAmount;
  /** 实收金额 */
  @Excel(name = "实收金额", scale = 2)
  @ApiModelProperty("实收金额")
  private BigDecimal actualAmount;
  /** 已收金额 */
  @Excel(name = "已收金额", scale = 2)
  @ApiModelProperty("已收金额")
  private BigDecimal totalReceivedAmount;
  /** 本次收费金额 */
  @Excel(name = "本次收费金额", scale = 2)
  @ApiModelProperty("本次收费金额")
  private BigDecimal receivedAmount;
  /** 会员卡本金 */
  @Excel(name = "会员卡本金", scale = 2)
  @ApiModelProperty("会员卡本金")
  private BigDecimal memberPrincipleAmount;
  /** 会员卡赠金 */
  @Excel(name = "会员卡赠金", scale = 2)
  @ApiModelProperty("会员卡赠金")
  private BigDecimal memberBonusAmount;
  /** 预付款本金 */
  @Excel(name = "预付款本金", scale = 2)
  @ApiModelProperty("预付款本金")
  private BigDecimal prepaidPrincipleAmount;
  /** 预付款赠金 */
  @Excel(name = "预付款赠金", scale = 2)
  @ApiModelProperty("预付款赠金")
  private BigDecimal prepaidBonusAmount;
  /** 现金 */
  @Excel(name = "现金", scale = 2)
  @ApiModelProperty("现金")
  private BigDecimal cashAmount;
  /** 支付宝 */
  @Excel(name = "支付宝", scale = 2)
  @ApiModelProperty("支付宝")
  private BigDecimal aliPayAmount;
  /** 微信 */
  @Excel(name = "微信", scale = 2)
  @ApiModelProperty("微信")
  private BigDecimal weChatAmount;
  /** 银行账户 */
  @Excel(name = "银行账户", scale = 2)
  @ApiModelProperty("银行账户")
  private BigDecimal bankAmount;
  /** 浙江省医保 */
  @Excel(name = "浙江省医保", scale = 2)
  @ApiModelProperty("浙江省医保")
  private BigDecimal zheJiangProvinceMedicalInsuranceAmount;
  /** 杭州市医保 */
  @Excel(name = "杭州市医保", scale = 2)
  @ApiModelProperty("杭州市医保")
  private BigDecimal hangZhouCityMedicalInsuranceAmount;
  /** 杭州市余杭医保 */
  @Excel(name = "杭州市余杭医保", scale = 2)
  @ApiModelProperty("杭州市余杭医保")
  private BigDecimal hangZhouYuHangMedicalInsuranceAmount;
  /** 杭州市萧山医保 */
  @Excel(name = "杭州市萧山医保", scale = 2)
  @ApiModelProperty("杭州市萧山医保")
  private BigDecimal hangZhouXiaoShanMedicalInsuranceAmount;
  /** Cigna */
  @Excel(name = "Cigna", scale = 2)
  @ApiModelProperty("Cigna")
  private BigDecimal cignaAmount;
  /** MSH */
  @Excel(name = "MSH", scale = 2)
  @ApiModelProperty("MSH")
  private BigDecimal mshAmount;
  /** AXA */
  @Excel(name = "AXA", scale = 2)
  @ApiModelProperty("AXA")
  private BigDecimal axaAmount;
  /** 风石 */
  @Excel(name = "风石", scale = 2)
  @ApiModelProperty("风石")
  private BigDecimal fengShiAmount;
  /** 本次免单支付 */
  @Excel(name = "本次免单支付", scale = 2)
  @ApiModelProperty("本次免单支付")
  private BigDecimal thisWaiverAmount;
  /** 艾维员工免单 */
  @Excel(name = "艾维员工免单", scale = 2)
  @ApiModelProperty("艾维员工免单")
  private BigDecimal employeeWaiverAmount;
  /** 收费人姓名 */
  @Excel(name = "收费人")
  @ApiModelProperty("收费人姓名")
  private String payeeName;
}
