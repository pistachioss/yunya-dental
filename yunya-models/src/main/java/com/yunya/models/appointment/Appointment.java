package com.yunya.models.appointment;

import javax.persistence.Column;
import javax.persistence.Id;
import java.util.Date;

public class Appointment {
  /** 唯一id */
  @Id private Integer id;

  /** 患者id */
  @Column(name = "p_id")
  private String pId;

  /** 患者姓名 */
  @Column(name = "name")
  private String name;

  /** 医生id */
  @Column(name = "dentist_id")
  private String dentistId;

  /** 医生姓名 */
  @Column(name = "dentist_name")
  private String dentistName;

  /** 助手id */
  @Column(name = "assistant_id")
  private String assistantId;

  /** 助手姓名 */
  @Column(name = "assistant_name")
  private String assistantName;

  /** 诊所id */
  @Column(name = "comp_clin_id")
  private String compClinId;

  /** 公司名称 */
  @Column(name = "comp_name")
  private String compName;

  /** 预约日期 */
  @Column(name = "appoint_date")
  private Date appointDate;

  /** 预约时间 */
  @Column(name = "appoint_time")
  private String appointTime;

  /** 预约开始时间 */
  @Column(name = "appoint_start_time")
  private Date appointStartTime;

  /** 预约结束时间 */
  @Column(name = "appoint_end_time")
  private Date appointEndTime;

  /** 预约开始——结束时间段 */
  @Column(name = "appoint_times")
  private String appointTimes;

  /** 时长(单位：分钟) */
  private Integer time;

  /** 预约类型,初诊/复诊 */
  @Column(name = "appoint_type")
  private Integer appointType;

  @Column(name = "status")
  private Byte status;

  public Byte getStatus() {
    return status;
  }

  public void setStatus(Byte status) {
    this.status = status;
  }

  /** 预约状态,0-新建,1-确认 */
  @Column(name = "appoint_state")
  private Boolean appointState;

  /** 预约科室id */
  @Column(name = "dept_id")
  private String deptId;

  @Column(name = "dept_name")
  private String deptName;

  /** 预约设备id */
  @Column(name = "device_id")
  private String deviceId;

  /** 设备名称 */
  @Column(name = "device_name")
  private String deviceName;

  /** 预约内容 */
  @Column(name = "content")
  private String content;

  /** 备注 */
  @Column(name = "remark")
  private String remark;

  /** 牙位 */
  @Column(name = "tooth_bit")
  private String toothBit;

  /** 预约项目代码 */
  @Column(name = "app_item_name")
  private String appItemName;

  /** 预约项目颜色 */
  @Column(name = "app_item_color")
  private String appItemColor;

  /** 创建人id */
  @Column(name = "crt_id")
  private String crtId;

  /** 创建人 */
  @Column(name = "crt_name")
  private String crtName;

  /** 创建时间 */
  @Column(name = "crt_time")
  private Date crtTime;

  /** 更新人id */
  @Column(name = "upd_id")
  private String updId;

  /** 最后更新人 */
  @Column(name = "upd_name")
  private String updName;

  /** 最后更新时间 */
  @Column(name = "upd_time")
  private Date updTime;

  /** 是否有效、是否启用、是否可见 */
  private Integer isvalid;

  /** 是否删除，逻辑假删除 */
  private Integer isdeleted;

  /**
   * 获取唯一id
   *
   * @return id - 唯一id
   */
  public Integer getId() {
    return id;
  }

  /**
   * 设置唯一id
   *
   * @param id 唯一id
   */
  public void setId(Integer id) {
    this.id = id;
  }

  /**
   * 获取患者id
   *
   * @return p_id - 患者id
   */
  public String getpId() {
    return pId;
  }

  /**
   * 设置患者id
   *
   * @param pId 患者id
   */
  public void setpId(String pId) {
    this.pId = pId;
  }

  /**
   * 获取医生id
   *
   * @return dentist_id - 医生id
   */
  public String getDentistId() {
    return dentistId;
  }

  /**
   * 设置医生id
   *
   * @param dentistId 医生id
   */
  public void setDentistId(String dentistId) {
    this.dentistId = dentistId;
  }

  /**
   * 获取助手id
   *
   * @return
   */
  public String getAssistantId() {
    return assistantId;
  }

  /**
   * 设置助手id
   *
   * @param assistantId
   */
  public void setAssistantId(String assistantId) {
    this.assistantId = assistantId;
  }

  /**
   * 获取助手姓名
   *
   * @return
   */
  public String getAssistantName() {
    return assistantName;
  }

  /**
   * 设置助手姓名
   *
   * @param assistantName
   */
  public void setAssistantName(String assistantName) {
    this.assistantName = assistantName;
  }

  /**
   * 获取诊所id
   *
   * @return comp_clin_id - 诊所id
   */
  public String getCompClinId() {
    return compClinId;
  }

  /**
   * 设置诊所id
   *
   * @param compClinId 诊所id
   */
  public void setCompClinId(String compClinId) {
    this.compClinId = compClinId;
  }

  /**
   * 获取公司名称
   *
   * @return
   */
  public String getCompName() {
    return compName;
  }

  /**
   * 设置公司名称
   *
   * @param compName
   */
  public void setCompName(String compName) {
    this.compName = compName;
  }

  /**
   * 获取预约日期
   *
   * @return appoint_date - 预约日期
   */
  public Date getAppointDate() {
    return appointDate;
  }

  /**
   * 设置预约日期
   *
   * @param appointDate 预约日期
   */
  public void setAppointDate(Date appointDate) {
    this.appointDate = appointDate;
  }

  /**
   * 获取预约时间
   *
   * @return appoint_time - 预约时间
   */
  public String getAppointTime() {
    return appointTime;
  }

  /**
   * 设置预约时间
   *
   * @param appointTime 预约时间
   */
  public void setAppointTime(String appointTime) {
    this.appointTime = appointTime;
  }

  /**
   * 获取预约开始时间
   *
   * @return appoint_start_time - 预约开始时间
   */
  public Date getAppointStartTime() {
    return appointStartTime;
  }

  /**
   * 设置预约开始时间
   *
   * @param appointStartTime 预约开始时间
   */
  public void setAppointStartTime(Date appointStartTime) {
    this.appointStartTime = appointStartTime;
  }

  /**
   * 获取预约结束时间
   *
   * @return appoint_end_time - 预约结束时间
   */
  public Date getAppointEndTime() {
    return appointEndTime;
  }

  /**
   * 设置预约结束时间
   *
   * @param appointEndTime 预约结束时间
   */
  public void setAppointEndTime(Date appointEndTime) {
    this.appointEndTime = appointEndTime;
  }

  /**
   * 获取时长(单位：分钟)
   *
   * @return time - 时长(单位：分钟)
   */
  public Integer getTime() {
    return time;
  }

  /**
   * 设置时长(单位：分钟)
   *
   * @param time 时长(单位：分钟)
   */
  public void setTime(Integer time) {
    this.time = time;
  }

  /**
   * 获取预约类型,初诊/复诊
   *
   * @return appoint_type - 预约类型,初诊/复诊
   */
  public Integer getAppointType() {
    return appointType;
  }

  /**
   * 设置预约类型,初诊/复诊
   *
   * @param appointType 预约类型,初诊/复诊
   */
  public void setAppointType(Integer appointType) {
    this.appointType = appointType;
  }

  /**
   * 获取预约状态
   *
   * @return appoint_state - 预约状态,0-未确认,1-确认
   */
  public Boolean getAppointState() {
    return appointState;
  }

  /**
   * 设置预约状态
   *
   * <p>appoint_state - 预约状态,0-未确认,1-确认
   */
  public void setAppointState(Boolean appointState) {
    this.appointState = appointState;
  }

  /**
   * 获取预约科室id
   *
   * @return dept_id - 预约科室id
   */
  public String getDeptId() {
    return deptId;
  }

  /**
   * 设置预约科室id
   *
   * @param deptId 预约科室id
   */
  public void setDeptId(String deptId) {
    this.deptId = deptId;
  }

  /**
   * 获取预约设备id
   *
   * @return device_id - 预约设备id
   */
  public String getDeviceId() {
    return deviceId;
  }

  /**
   * 设置预约设备id
   *
   * @param deviceId 预约设备id
   */
  public void setDeviceId(String deviceId) {
    this.deviceId = deviceId;
  }

  /**
   * 获取备注
   *
   * @return remark - 备注
   */
  public String getRemark() {
    return remark;
  }

  /**
   * 设置备注
   *
   * @param remark 备注
   */
  public void setRemark(String remark) {
    this.remark = remark;
  }

  /**
   * 获取牙位
   *
   * @return tooth_bit - 牙位
   */
  public String getToothBit() {
    return toothBit;
  }

  /**
   * 设置牙位
   *
   * @param toothBit 牙位
   */
  public void setToothBit(String toothBit) {
    this.toothBit = toothBit;
  }

  /** 获取项目名称 */
  public String getAppItemName() {
    return appItemName;
  }

  /**
   * 设置项目名称
   *
   * @param appItemName 项目名称
   */
  public void setAppItemName(String appItemName) {
    this.appItemName = appItemName;
  }

  /** 获取项目颜色 */
  public String getAppItemColor() {
    return appItemColor;
  }

  /**
   * 设置项目颜色
   *
   * @param appItemColor 当前项目颜色
   */
  public void setAppItemColor(String appItemColor) {
    this.appItemColor = appItemColor;
  }

  /**
   * 获取创建人id
   *
   * @return crt_id - 创建人id
   */
  public String getCrtId() {
    return crtId;
  }

  /**
   * 设置创建人id
   *
   * @param crtId 创建人id
   */
  public void setCrtId(String crtId) {
    this.crtId = crtId;
  }

  /**
   * 获取创建人
   *
   * @return crt_name - 创建人
   */
  public String getCrtName() {
    return crtName;
  }

  /**
   * 设置创建人
   *
   * @param crtName 创建人
   */
  public void setCrtName(String crtName) {
    this.crtName = crtName;
  }

  /**
   * 获取创建时间
   *
   * @return crt_time - 创建时间
   */
  public Date getCrtTime() {
    return crtTime;
  }

  /**
   * 设置创建时间
   *
   * @param crtTime 创建时间
   */
  public void setCrtTime(Date crtTime) {
    this.crtTime = crtTime;
  }

  /**
   * 获取更新人id
   *
   * @return upd_id - 更新人id
   */
  public String getUpdId() {
    return updId;
  }

  /**
   * 设置更新人id
   *
   * @param updId 更新人id
   */
  public void setUpdId(String updId) {
    this.updId = updId;
  }

  /**
   * 获取最后更新人
   *
   * @return upd_name - 最后更新人
   */
  public String getUpdName() {
    return updName;
  }

  /**
   * 设置最后更新人
   *
   * @param updName 最后更新人
   */
  public void setUpdName(String updName) {
    this.updName = updName;
  }

  /**
   * 获取最后更新时间
   *
   * @return upd_time - 最后更新时间
   */
  public Date getUpdTime() {
    return updTime;
  }

  /**
   * 设置最后更新时间
   *
   * @param updTime 最后更新时间
   */
  public void setUpdTime(Date updTime) {
    this.updTime = updTime;
  }

  /**
   * 获取是否有效、是否启用、是否可见
   *
   * @return isvalid - 是否有效、是否启用、是否可见
   */
  public Integer getIsvalid() {
    return isvalid;
  }

  /**
   * 设置是否有效、是否启用、是否可见
   *
   * @param isvalid 是否有效、是否启用、是否可见
   */
  public void setIsvalid(Integer isvalid) {
    this.isvalid = isvalid;
  }

  /**
   * 获取是否删除，逻辑假删除
   *
   * @return isdeleted - 是否删除，逻辑假删除
   */
  public Integer getIsdeleted() {
    return isdeleted;
  }

  /**
   * 设置是否删除，逻辑假删除
   *
   * @param isdeleted 是否删除，逻辑假删除
   */
  public void setIsdeleted(Integer isdeleted) {
    this.isdeleted = isdeleted;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getDentistName() {
    return dentistName;
  }

  public void setDentistName(String dentistName) {
    this.dentistName = dentistName;
  }

  public String getDeptName() {
    return deptName;
  }

  public void setDeptName(String deptName) {
    this.deptName = deptName;
  }

  public String getDeviceName() {
    return deviceName;
  }

  public void setDeviceName(String deviceName) {
    this.deviceName = deviceName;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAppointTimes() {
    return appointTimes;
  }

  public void setAppointTimes(String appointTimes) {
    this.appointTimes = appointTimes;
  }
}
