package com.yunya.models.system;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "sys_employee")
public class SysEmployee {
  @Id private Integer id;

  /** 用户ID */
  @Column(name = "user_id")
  private Integer userId;

  /** 员工姓名 */
  private String name;

  /** 手机 */
  @Column(name = "mobile_phone")
  private String mobilePhone;

  /** 固定电话 */
  private String telephone;

  /** 性别: 0, 男; 1, 女 */
  private Byte gender;

  /** 身份证 */
  private String identity;

  /** 试用: 0, 正式: 1，实习: 2, 离职 */
  @Column(name = "work_status")
  private Byte workStatus;

  /** 工作类型（兼职-0；全职-1） */
  @Column(name = "work_type")
  private Byte workType;

  /** 合同签署日期 */
  @Column(name = "contract_signing_date")
  private Date contractSigningDate;

  /** 入职时间 */
  @Column(name = "entry_date")
  private Date entryDate;

  /** 毕业院校 */
  @Column(name = "graduated_school")
  private String graduatedSchool;

  /** 学历 */
  private Integer education;

  /** 离职日期 */
  @Column(name = "leave_time")
  private Date leaveTime;

  /** 职称 */
  private String title;

  /** 职级 */
  @Column(name = "post_level")
  private String postLevel;

  /** 紧急联系人姓名 */
  @Column(name = "emergency_contact")
  private String emergencyContact;

  /** 紧急联系人联系人电话 */
  @Column(name = "emergency_contact_phone")
  private String emergencyContactPhone;

  /** 是否有员工折扣 */
  private Boolean discount;

  /** 个人照片链接 */
  private String photo;

  /** 毕业证书照片:多张照片用;隔开 */
  @Column(name = "diploma_photo")
  private String diplomaPhoto;

  /** 工号 */
  @Column(name = "work_number")
  private String workNumber;

  /** 创建人ID */
  @Column(name = "crt_id")
  private Integer crtId;

  /** 创建人名称 */
  @Column(name = "crt_name")
  private String crtName;

  /** 创建时间 */
  @Column(name = "crt_time")
  private Date crtTime;

  @Column(name = "upd_id")
  private Integer updId;

  /** 修改人名称 */
  @Column(name = "upd_name")
  private String updName;

  /** 修改时间 */
  @Column(name = "upd_time")
  private Date updTime;

  /** @return id */
  public Integer getId() {
    return id;
  }

  /** @param id */
  public void setId(Integer id) {
    this.id = id;
  }

  /**
   * 获取用户ID
   *
   * @return user_id - 用户ID
   */
  public Integer getUserId() {
    return userId;
  }

  /**
   * 设置用户ID
   *
   * @param userId 用户ID
   */
  public void setUserId(Integer userId) {
    this.userId = userId;
  }

  /**
   * 获取员工姓名
   *
   * @return name - 员工姓名
   */
  public String getName() {
    return name;
  }

  /**
   * 设置员工姓名
   *
   * @param name 员工姓名
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * 获取手机
   *
   * @return mobile_phone - 手机
   */
  public String getMobilePhone() {
    return mobilePhone;
  }

  /**
   * 设置手机
   *
   * @param mobilePhone 手机
   */
  public void setMobilePhone(String mobilePhone) {
    this.mobilePhone = mobilePhone;
  }

  /**
   * 获取固定电话
   *
   * @return telephone
   */
  public String getTelephone() {
    return telephone;
  }

  /**
   * 设置固定电话
   *
   * @param telephone -固定电话
   */
  public void setTelephone(String telephone) {
    this.telephone = telephone;
  }

  /**
   * 获取性别: 0, 男; 1, 女
   *
   * @return sex - 性别: 0, 男; 1, 女
   */
  public Byte getGender() {
    return gender;
  }

  /**
   * 设置性别: 0, 男; 1, 女
   *
   * @param gender 性别: 0, 男; 1, 女
   */
  public void setGender(Byte gender) {
    this.gender = gender;
  }

  /**
   * 获取身份证
   *
   * @return identity - 身份证
   */
  public String getIdentity() {
    return identity;
  }

  /**
   * 设置身份证
   *
   * @param identity 身份证
   */
  public void setIdentity(String identity) {
    this.identity = identity;
  }

  /**
   * 获取试用: 0, 正式: 1，实习: 2, 离职
   *
   * @return work_status - 试用: 0, 正式: 1，实习: 2, 离职
   */
  public Byte getWorkStatus() {
    return workStatus;
  }

  /**
   * 设置试用: 0, 正式: 1，实习: 2, 离职
   *
   * @param workStatus 试用: 0, 正式: 1，实习: 2, 离职
   */
  public void setWorkStatus(Byte workStatus) {
    this.workStatus = workStatus;
  }

  /**
   * 获取工作类型（兼职-0；全职-1）
   *
   * @return work_type - 工作类型（兼职-0；全职-1）
   */
  public Byte getWorkType() {
    return workType;
  }

  /**
   * 设置工作类型（兼职-0；全职-1）
   *
   * @param workType 工作类型（兼职-0；全职-1）
   */
  public void setWorkType(Byte workType) {
    this.workType = workType;
  }

  /**
   * 获取合同签署日期
   *
   * @return contract_signing_date - 合同签署日期
   */
  public Date getContractSigningDate() {
    return contractSigningDate;
  }

  /**
   * 设置合同签署日期
   *
   * @param contractSigningDate 合同签署日期
   */
  public void setContractSigningDate(Date contractSigningDate) {
    this.contractSigningDate = contractSigningDate;
  }

  /**
   * 获取入职时间
   *
   * @return entry_date - 入职时间
   */
  public Date getEntryDate() {
    return entryDate;
  }

  /**
   * 设置入职时间
   *
   * @param entryDate 入职时间
   */
  public void setEntryDate(Date entryDate) {
    this.entryDate = entryDate;
  }

  /**
   * 获取毕业院校
   *
   * @return graduated_school - 毕业院校
   */
  public String getGraduatedSchool() {
    return graduatedSchool;
  }

  /**
   * 设置毕业院校
   *
   * @param graduatedSchool 毕业院校
   */
  public void setGraduatedSchool(String graduatedSchool) {
    this.graduatedSchool = graduatedSchool;
  }

  /**
   * 获取学历
   *
   * @return education - 学历
   */
  public Integer getEducation() {
    return education;
  }

  /**
   * 设置学历
   *
   * @param education 学历
   */
  public void setEducation(Integer education) {
    this.education = education;
  }

  /**
   * 获取离职日期
   *
   * @return leave_time - 离职日期
   */
  public Date getLeaveTime() {
    return leaveTime;
  }

  /**
   * 设置离职日期
   *
   * @param leaveTime 离职日期
   */
  public void setLeaveTime(Date leaveTime) {
    this.leaveTime = leaveTime;
  }

  /**
   * 获取职称
   *
   * @return title - 职称
   */
  public String getTitle() {
    return title;
  }

  /**
   * 设置职称
   *
   * @param title 职称
   */
  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * 获取职级
   *
   * @return post_level - 职级
   */
  public String getPostLevel() {
    return postLevel;
  }

  /**
   * 设置职级
   *
   * @param postLevel 职级
   */
  public void setPostLevel(String postLevel) {
    this.postLevel = postLevel;
  }

  /**
   * 获取紧急联系人姓名
   *
   * @return emergency_contact - 紧急联系人姓名
   */
  public String getEmergencyContact() {
    return emergencyContact;
  }

  /**
   * 设置紧急联系人姓名
   *
   * @param emergencyContact 紧急联系人姓名
   */
  public void setEmergencyContact(String emergencyContact) {
    this.emergencyContact = emergencyContact;
  }

  /**
   * 获取紧急联系人联系人电话
   *
   * @return emergency_contact_phone - 紧急联系人联系人电话
   */
  public String getEmergencyContactPhone() {
    return emergencyContactPhone;
  }

  /**
   * 设置紧急联系人联系人电话
   *
   * @param emergencyContactPhone 紧急联系人联系人电话
   */
  public void setEmergencyContactPhone(String emergencyContactPhone) {
    this.emergencyContactPhone = emergencyContactPhone;
  }

  /**
   * 获取是否有员工折扣
   *
   * @return discount - 是否有员工折扣
   */
  public Boolean getDiscount() {
    return discount;
  }

  /**
   * 设置是否有员工折扣
   *
   * @param discount 是否有员工折扣
   */
  public void setDiscount(Boolean discount) {
    this.discount = discount;
  }

  /**
   * 获取个人照片链接
   *
   * @return photo - 个人照片链接
   */
  public String getPhoto() {
    return photo;
  }

  /**
   * 设置个人照片链接
   *
   * @param photo 个人照片链接
   */
  public void setPhoto(String photo) {
    this.photo = photo;
  }

  /**
   * 获取毕业证书照片:多张照片用;隔开
   *
   * @return diploma_photo - 毕业证书照片:多张照片用;隔开
   */
  public String getDiplomaPhoto() {
    return diplomaPhoto;
  }

  /**
   * 设置毕业证书照片:多张照片用;隔开
   *
   * @param diplomaPhoto 毕业证书照片:多张照片用;隔开
   */
  public void setDiplomaPhoto(String diplomaPhoto) {
    this.diplomaPhoto = diplomaPhoto;
  }

  /**
   * 获取工号
   *
   * @return work_number - 工号
   */
  public String getWorkNumber() {
    return workNumber;
  }

  /**
   * 设置工号
   *
   * @param workNumber 工号
   */
  public void setWorkNumber(String workNumber) {
    this.workNumber = workNumber;
  }

  /**
   * 获取创建人ID
   *
   * @return crt_id - 创建人ID
   */
  public Integer getCrtId() {
    return crtId;
  }

  /**
   * 设置创建人ID
   *
   * @param crtId 创建人ID
   */
  public void setCrtId(Integer crtId) {
    this.crtId = crtId;
  }

  /**
   * 获取创建人名称
   *
   * @return crt_name - 创建人名称
   */
  public String getCrtName() {
    return crtName;
  }

  /**
   * 设置创建人名称
   *
   * @param crtName 创建人名称
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

  /** @return upd_id */
  public Integer getUpdId() {
    return updId;
  }

  /** @param updId */
  public void setUpdId(Integer updId) {
    this.updId = updId;
  }

  /**
   * 获取修改人名称
   *
   * @return upd_name - 修改人名称
   */
  public String getUpdName() {
    return updName;
  }

  /**
   * 设置修改人名称
   *
   * @param updName 修改人名称
   */
  public void setUpdName(String updName) {
    this.updName = updName;
  }

  /**
   * 获取修改时间
   *
   * @return upd_time - 修改时间
   */
  public Date getUpdTime() {
    return updTime;
  }

  /**
   * 设置修改时间
   *
   * @param updTime 修改时间
   */
  public void setUpdTime(Date updTime) {
    this.updTime = updTime;
  }
}
