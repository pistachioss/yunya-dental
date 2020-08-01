package com.yunya.models.appointment;

import lombok.Data;
import lombok.ToString;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

@Data
@ToString
public class Appointment {
  /** 主键 */
  @Id
  @GeneratedValue
  private Integer id ;
  /** 患者ID */
  private Integer patientId ;
  /** 诊所ID */
  private Integer orgId ;
  /** 医生ID */
  private Integer dentistId ;
  /** 助手ID;默认医生配置助手ID */
  private Integer assistantId ;
  /** 门诊科室ID;默认医生配置科室ID */
  private Integer clinicDeptRoomId ;
  /** 门诊设备ID */
  private Integer clinicDeviceItemId ;
  /** 预约项目ID */
  private Integer clinicAppointItemId ;
  /** 预约总时长;默认取预约项目时长 */
  private Integer appointDuration ;
  /** 预约日期 */
  private Date appointDate ;
  /** 预约时间 */
  private String appointTime ;
  /** 预约开始时间 */
  private Date appointStartTime ;
  /** 预约结束时间 */
  private Date appointEndTime ;
  /** 预约时间段;预约开始时间-预约结束时间 */
  private String appointPeriod ;
  /** 牙位 */
  private String toothBit ;
  /** 预约内容 */
  private String appointContent ;
  /** 预约类型;0-初诊预约；1-复诊预约 */
  private Integer appointType ;
  /** 预约确认;0-未确认；1-确认 */
  private String confirmStatus ;
  /** 预约状态;0-预约未到，1-履约，2，取消预约，3-失约 */
  private Integer appointStatus ;
  /** 备注;备注 */
  private String remarks ;
  /** 是否启用;是否有效 */
  private String inservice ;
  /** 创建人ID */
  private Integer crtId ;
  /** 创建人姓名 */
  private String crtName ;
  /** 创建时间 */
  private Date crtTime ;
  /** 更新人ID */
  private Integer uptId ;
  /** 更新人姓名 */
  private String updName ;
  /** 更新时间 */
  private Date updTime ;
}
