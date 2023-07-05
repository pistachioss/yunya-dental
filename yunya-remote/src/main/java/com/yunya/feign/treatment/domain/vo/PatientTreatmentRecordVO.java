package com.yunya.feign.treatment.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介: 患者就诊记录信息VO
 *
 * @author: chow
 * @date: 2020/9/11 11:00
 * @description:
 * @since: 1.0.0
 */
@ApiModel("患者就诊记录信息VO-患者档案")
@Data
@ToString
public class PatientTreatmentRecordVO implements Serializable {
  /** 就诊记录ID */
  @ApiModelProperty("就诊记录ID")
  private Integer treatmentRecordId;
  /** 就诊日期 */
  @ApiModelProperty("就诊日期")
  private String treatmentDate;
  /** 初/复诊 */
  @ApiModelProperty("初/复诊-0:出诊；1-复诊")
  private Byte firstVisit;
  /** 接诊医生ID */
  @ApiModelProperty("接诊医生ID")
  private Integer dentistId;
  /** 医生姓名 */
  @ApiModelProperty("医生姓名")
  private String dentistName;
  /** 就诊诊所ID */
  @ApiModelProperty("就诊诊所ID")
  private Integer orgId;
  /** 诊所名称 */
  @ApiModelProperty("诊所名称")
  private String orgName;
  /** 助手1ID */
  @ApiModelProperty("助手1ID")
  private Integer assistantId1;
  /** 助手1姓名 */
  @ApiModelProperty("助手1姓名")
  private String assistantName1;
  /** 助手2ID */
  @ApiModelProperty("助手2ID")
  private Integer assistantId2;
  /** 助手2姓名 */
  @ApiModelProperty("助手2姓名")
  private String assistantName2;
  /** 助手3ID */
  @ApiModelProperty("助手3ID")
  private Integer assistantId3;
  /** 助手2姓名 */
  @ApiModelProperty("助手3姓名")
  private String assistantName3;
  /** 就诊状态 */
  @ApiModelProperty("就诊状态 0-接诊中;1-已开单;2-接诊完成3-已结账")
  private Byte treatmentStatus;
  /** 开单记录ID */
  @ApiModelProperty("开单记录ID")
  private Integer orderRecordId;
  /** 原价合计 */
  @ApiModelProperty("原价合计")
  private BigDecimal originalPrice;
  /** 账单记录ID */
  @ApiModelProperty("账单记录ID")
  private Integer billRecordId;
  /** 应收金额 */
  @ApiModelProperty("应收金额")
  private BigDecimal actualReceivableAmount;
  /** 实收金额 */
  @ApiModelProperty("实收金额")
  private BigDecimal receivedAmount;
  /** 划扣金额合计 */
  @ApiModelProperty("划扣金额合计")
  private BigDecimal swipeAmount;
  /** 本单欠费金额 */
  @ApiModelProperty("本单欠费金额")
  private BigDecimal debtAmount;
  /** 免单支付金额 */
  @ApiModelProperty("免单支付金额")
  private BigDecimal freePaymentPrice;
  /** 预约ID */
  @ApiModelProperty("预约ID")
  private Integer appointmentId;
  /** 挂号ID */
  @ApiModelProperty("挂号ID")
  private Integer registeredId;
  /** 优惠类型 */
  @ApiModelProperty("优惠类型")
  private Byte privilegeType;

  @ApiModelProperty("开单项目列表")
  private String billItems;
}
