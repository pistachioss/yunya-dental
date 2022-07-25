package com.yunya.feign.report.domain.vo;

import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介:
 *
 * @author: chow
 * @date: 2022/7/24 15:09
 * @description:
 * @since: 1.0.0
 */
@Data
@ApiModel("产品使用记录视图模型")
public class CardConsumeRecordVO implements Serializable {
  @ApiModelProperty("卡券id")
  private Integer cardId;

  @ApiModelProperty("产品ID")
  private Integer couponId;

  @ApiModelProperty("产品名称")
  @Excel(name = "产品名称")
  private String couponName;

  @ApiModelProperty("销售渠道ID")
  private Integer saleChannelId;

  @ApiModelProperty("销售渠道名称")
  @Excel(name = "销售渠道")
  private String saleChannelName;

  @ApiModelProperty("卡券激活日期")
  @Excel(name = "激活日期")
  private String activationDate;

  @ApiModelProperty("卡券使用日期")
  @Excel(name = "使用时间")
  private String cardUseDate;

  @ApiModelProperty("使用门诊ID")
  private Integer cardUseOrgId;

  @ApiModelProperty("使用门诊名称")
  @Excel(name = "使用门诊")
  private String cardUseOrgName;

  @ApiModelProperty("本次开单金额")
  @Excel(name = "本单原价")
  private BigDecimal orderAmount;

  @ApiModelProperty("本次优惠金额")
  @Excel(name = "本单优惠", cellType = Excel.ColumnType.NUMERIC)
  private BigDecimal benefitAmount;

  @ApiModelProperty("账单实收金额")
  @Excel(name = "本单实收", cellType = Excel.ColumnType.NUMERIC)
  private BigDecimal receivedAmount;

  @ApiModelProperty("操作人ID")
  private Integer operatorId;

  @ApiModelProperty("操作人名称")
  @Excel(name = "操作人")
  private String operatorName;

  @ApiModelProperty("患者ID")
  private Integer patientId;

  @ApiModelProperty("患者姓名")
  @Excel(name = "患者姓名")
  private String patientName;

  @ApiModelProperty("病历号")
  @Excel(name = "病历号")
  private String medicalNum;

  @ApiModelProperty("患者手机号")
  @Excel(name = "手机号")
  private String mobile;

  @ApiModelProperty("年龄")
  @Excel(name = "年龄")
  private Integer age;

  @ApiModelProperty("性别(0-男；1-女;2-未知)")
  @Excel(name = "性别", readConverterExp = "0=男,1=女,2-未知")
  private Byte gender;

  @ApiModelProperty("患者来源分类ID")
  private Integer patientOriginTypeId;

  @ApiModelProperty("患者来源分类名称")
  @Excel(name = "患者来源分类")
  private String patientOriginTypeName;

  @ApiModelProperty("患者来源ID")
  private Integer patientOriginId;

  @ApiModelProperty("患者来源名称")
  @Excel(name = "患者来源")
  private String patientOriginName;

  @ApiModelProperty("初诊门诊ID")
  private Integer firstVisitOrgId;

  @ApiModelProperty("初诊门诊名称")
  @Excel(name = "初诊门诊")
  private String firstVisitOrgName;

  @ApiModelProperty("初诊日期")
  @Excel(name = "初诊日期")
  private String firstVisitDate;

  @ApiModelProperty("初诊医生ID")
  private Integer firstVisitDentistId;

  @ApiModelProperty("初诊医生姓名")
  @Excel(name = "初诊医生")
  private String firstVisitDentistName;

  @ApiModelProperty("累计消费总额")
  @Excel(name = "累计消费", cellType = Excel.ColumnType.NUMERIC)
  private BigDecimal totalConsumeAmount;

  @ApiModelProperty("累计欠费总额")
  @Excel(name = "累计欠费", cellType = Excel.ColumnType.NUMERIC)
  private BigDecimal totalDebtAmount;
}
