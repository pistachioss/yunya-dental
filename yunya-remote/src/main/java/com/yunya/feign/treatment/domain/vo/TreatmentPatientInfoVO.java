package com.yunya.feign.treatment.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介: 就诊列表患者信息VO
 *
 * @author: chow
 * @date: 2020/8/12 16:05
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("就诊列表患者信息VO")
public class TreatmentPatientInfoVO implements Serializable {
  /** 接诊记录ID */
  @ApiModelProperty("就诊记录ID")
  private Integer id;
  /** 诊所id */
  @ApiModelProperty("诊所id")
  private Integer orgId;
  /** 初/复诊 */
  @ApiModelProperty("初/复诊")
  private Byte firstVisit;
  /******************************* 患者信息 ********************************/
  /** 患者id */
  @ApiModelProperty("患者id")
  private Integer patientId;
  /** 患者姓名 */
  @ApiModelProperty("患者姓名")
  private String patientName;
  /** 患者手机号 */
  @ApiModelProperty("患者手机号")
  private String mobile;
  /** 患者出生日期 */
  @ApiModelProperty("患者出生日期")
  private String birthday;
  /** 性别 */
  @ApiModelProperty("性别")
  private Byte gender;
  /** 年龄 */
  @ApiModelProperty("年龄")
  private Integer age;
  /** 患者病历号 */
  @ApiModelProperty("患者病历号")
  private String medicalNumber;
  /** 患者类型 */
  @ApiModelProperty("患者类型")
  private String patientKind;
  /** 患者过敏原 */
  @ApiModelProperty("患者过敏原")
  private String allergen;
  /** 欠费总额 */
  @ApiModelProperty("欠费总额")
  private BigDecimal arrears;
  /** 会员图标 */
  @ApiModelProperty("会员图标")
  private String memberIcon;
  /** 会员卡名 */
  @ApiModelProperty("会员卡类型名称")
  private String memberCardName;
  /** 患者备注 */
  @ApiModelProperty("患者备注")
  private String patientRemark;
  /********************************  预约信息 *********************************/
  /** 预约id */
  @ApiModelProperty("预约id")
  private Integer appointmentId;
  /** 预约医生id */
  @ApiModelProperty("预约医生id")
  private Integer appointDentistId;
  /** 预约医生姓名 */
  @ApiModelProperty("预约医生姓名")
  private String appointDentistName;
  /** 预约助手id */
  @ApiModelProperty("预约助手id")
  private Integer appointAssistantId;
  /** 预约助手姓名 */
  @ApiModelProperty("预约助手姓名")
  private String appointAssistantName;
  /** 预约科室ID */
  @ApiModelProperty("预约科室ID")
  private Integer appointDeptRoomId;
  /** 预约科室名称 */
  @ApiModelProperty("预约科室名称")
  private String appointDeptRoomName;
  /** 预约时间 */
  @ApiModelProperty("预约时间")
  private String appointTime;
  /** 预约时长 */
  @ApiModelProperty("预约时长")
  private Integer appointDuration;
  /** 预约内容 */
  @ApiModelProperty("预约内容")
  private String appointContent;
  /** 预约备注 */
  @ApiModelProperty("预约备注")
  private String appointRemark;
  /** 预约类型（初/复诊） */
  @ApiModelProperty("预约类型（初/复诊）")
  private Byte appointType;
  /** 预约状态 */
  @ApiModelProperty("预约状态")
  private Byte appointStatus;
  /** 预约确认 */
  @ApiModelProperty("预约确认")
  private Boolean confirmStatus;
  /******************************** 挂号信息 *******************************/
  /** 挂号ID */
  @ApiModelProperty("挂号ID")
  private Integer registeredId;
  /** 挂号医生id */
  @ApiModelProperty("挂号医生id")
  private Integer regDentistId;
  /** 挂号医生姓名 */
  @ApiModelProperty("挂号医生姓名")
  private String regDentistName;
  /** 挂号助手id */
  @ApiModelProperty("挂号助手id")
  private Integer regAssistantId;
  /** 挂号助手名称 */
  @ApiModelProperty("挂号助手名称")
  private String regAssistantName;
  /** 挂号科室ID */
  @ApiModelProperty("挂号科室ID")
  private Integer regDeptRoomId;
  /** 挂号科室名称 */
  @ApiModelProperty("挂号科室名称")
  private String regDeptRoomName;
  /** 挂号日期 */
  @ApiModelProperty("挂号日期")
  private String regDate;
  /** 挂号时间 */
  @ApiModelProperty("挂号时间")
  private String regTime;
  /******************************* 接诊信息 ********************************/
  /** 接诊医生ID */
  @ApiModelProperty("接诊医生ID")
  private Integer treatDentistId;
  /** 接诊医生姓名 */
  @ApiModelProperty("接诊医生姓名")
  private String treatDentistName;
  /** 就诊日期 */
  @ApiModelProperty("就诊日期")
  private String treatDate;
  /** 开始接诊时间 */
  @ApiModelProperty("开始接诊时间")
  private String treatStartTime;
  /** 治疗完成时间 */
  @ApiModelProperty("治疗完成时间")
  private String treatEndTime;
  /** 诊疗状态 */
  @ApiModelProperty("诊疗状态(0-接诊中;1-已开单;2-接诊完成3-已结账)")
  private Byte treatmentStatus;
  /** 后续预约 */
  @ApiModelProperty("后续预约")
  private Integer nextAppointment;
  /** 后续随访 */
  @ApiModelProperty("后续随访")
  private Integer nextInterview;
  /** 书写病历 */
  @ApiModelProperty("书写病历")
  private Boolean medicalRecordCompleted;
  /***************************** 账单信息 *********************************/
  /** 账单ID */
  @ApiModelProperty("开单记录ID")
  private Integer orderRecordId;
  /** 原价合计 */
  @ApiModelProperty("原价合计")
  private BigDecimal originalPrice;
  /** 账单（开单）状态 */
  @ApiModelProperty("订单状态（0-账单未锁定 ；1-账单锁定；2-结算完成状态；3-收费中）")
  private Byte orderStatus;
  /** 本次收费总额 */
  @ApiModelProperty("本次收费总额")
  private BigDecimal receivedAmount;
}
