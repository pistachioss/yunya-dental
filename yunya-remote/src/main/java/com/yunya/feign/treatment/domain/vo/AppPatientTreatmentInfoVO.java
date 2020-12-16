package com.yunya.feign.treatment.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import org.springframework.web.context.annotation.ApplicationScope;

import java.io.Serializable;

/**
 * 简介: APP端患者就诊信息VO
 *
 * @author: chow
 * @date: 2020/10/11 16:19
 * @description:
 * @since: 1.0.0
 */
@ApiModel("APP端患者就诊信息VO")
@Data
@ToString
public class AppPatientTreatmentInfoVO implements Serializable {
  /** 组织ID */
  @ApiModelProperty("组织ID")
  private Integer orgId;
  /** 组织名称 */
  @ApiModelProperty("组织名称")
  private String orgName;
  /** 患者id */
  @ApiModelProperty("患者id")
  private Integer patientId;
  /** 患者姓名 */
  @ApiModelProperty("患者姓名")
  private String patientName;
  /** 患者性别 */
  @ApiModelProperty("患者性别")
  private Byte gender;
  /** 患者年龄 */
  @ApiModelProperty("患者年龄")
  private Integer age;
  /** 医生id */
  @ApiModelProperty("医生id")
  private Integer dentistId;
  /** 医生姓名（预约医生/挂号医生） */
  @ApiModelProperty("医生姓名（与诊疗状态相关：当treatStatus为 0-预约未确认；1-预约确认时为预约医生），其余状态为挂号/接诊医生")
  private String dentistName;
  /** 助手ID */
  @ApiModelProperty("助手ID")
  private Integer assistantId;
  /** 助手姓名 */
  @ApiModelProperty("助手姓名")
  private String assistantName;
  /** 诊疗状态 */
  @ApiModelProperty("诊疗状态 0or1-预约未到 2-候诊中 3-就诊中 4-治疗完成 5-已结账")
  private Byte treatStatus;
  /** 节点时间（预约时间/挂号时间/就诊时间/就诊完成时间） */
  @ApiModelProperty("节点时间，与就诊状态有关（0or1-预约时间/2-挂号时间/3-就诊时间/4-就诊完成时间/5-结账时间）")
  private String nodeTime;
  /** 预约ID */
  @ApiModelProperty("预约ID")
  private Integer appointId;
  /** 挂号ID */
  @ApiModelProperty("挂号ID")
  private Integer registedId;
  /** 账单记录ID */
  private Integer orderRecordId;

}
