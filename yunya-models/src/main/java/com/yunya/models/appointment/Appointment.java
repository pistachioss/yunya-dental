package com.yunya.models.appointment;

import lombok.Cleanup;
import lombok.Data;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

@Data
@ToString
public class Appointment {
  @Id
  /** 主键 */
  private Integer id ;
  /** 患者ID */
  @Column(name = "patient_id")
  private Integer patientId ;
  /** 诊所ID */
  @Column(name= "org_id")
  private Integer orgId ;
  /** 医生ID */
  @Column(name = "dentist_id")
  private Integer dentistId ;
  /** 助手ID;默认医生配置助手ID */
  @Column(name = "assistant_id")
  private Integer assistantId ;
  /** 门诊科室ID;默认医生配置科室ID */
  @Column(name = "clinic_dept_room_id")
  private Integer clinicDeptRoomId ;
  /** 门诊设备ID */
  @Column(name = "clinic_device_item_id")
  private Integer clinicDeviceItemId ;
  /** 预约项目ID */
  @Column(name = "clinic_appoint_item_id")
  private Integer clinicAppointItemId ;
  /** 预约总时长;默认取预约项目时长 */
  @Column(name = "appoint_duration")
  private Integer appointDuration ;
  /** 预约日期 */
  @Column(name = "appoint_date")
  private Date appointDate ;
  /** 预约时间 */
  @Column(name = "appoint_time")
  private String appointTime ;
  /** 预约开始时间 */
  @Column(name = "appoint_start_time")
  private Date appointStartTime ;
  /** 预约结束时间 */
  @Column(name = "appoint_end_time")
  private Date appointEndTime ;
  /** 预约时间段;预约开始时间-预约结束时间 */
  @Column(name = "appoint_period")
  private String appointPeriod ;
  /** 牙位 */
  @Column(name = "tooth_bit")
  private String toothBit ;
  /** 预约内容 */
  @Column(name = "appoint_content")
  private String appointContent ;
  /** 预约类型;0-初诊预约；1-复诊预约 */
  @Column(name = "appoint_type")
  private Byte appointType ;
  /** 预约确认;0-未确认；1-确认 */
  @Column(name = "confirm_status")
  private Byte confirmStatus ;
  /** 预约状态;0-预约未到，1-履约，2，取消预约，3-失约 */
  @Column(name = "appoint_status")
  private Byte appointStatus ;
  /** 备注;备注 */
  @Column(name = "remarks")
  private String remarks ;
  /** 是否启用;是否有效 */
  @Column(name = "inservice")
  private Byte inservice ;
  /** 创建人ID */
  @Column(name = "crt_id")
  private Integer crtId ;
  /** 创建人姓名 */
  @Column(name = "crt_name")
  private String crtName ;
  /** 创建时间 */
  @Column(name = "crt_time")
  private Date crtTime ;
  /** 更新人ID */
  @Column(name = "upt_id")
  private Integer uptId ;
  /** 更新人姓名 */
  @Column(name = "upd_name")
  private String updName ;
  /** 更新时间 */
  @Column(name = "upd_time")
  private Date updTime ;
}
