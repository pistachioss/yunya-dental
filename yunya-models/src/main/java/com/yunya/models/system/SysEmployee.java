package com.yunya.models.system;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
import javax.persistence.*;

@Table(name = "sys_employee")
public class SysEmployee {
  @Id
  private Integer id;

  /**
   * 用户ID
   */
  @Column(name = "user_id")
  private Integer userId;

  /**
   * 员工姓名
   */
  private String name;

  /**
   * 姓名拼音首字母缩写
   */
  private String pinyin;

  /**
   * 性别: 0, 男; 1, 女
   */
  private Byte gender;

  /**
   * 手机
   */
  @Column(name = "mobile_phone")
  private String mobilePhone;

  /**
   * 固定电话
   */
  private String telephone;

  /**
   * 身份证
   */
  private String identity;

  /**
   * 生日
   */
  private String birthday;

  /**
   * 居住地址-家庭地址
   */
  private String address;

  /**
   * 试用: 0, 正式: 1， 离职:2；实习: 3；
   */
  @Column(name = "work_status")
  private Byte workStatus;

  /**
   * 工作类型（兼职-0；全职-1）
   */
  @Column(name = "work_type")
  private Byte workType;

  /**
   * 合同签署日期
   */
  @Column(name = "contract_signing_date")
  private String contractSigningDate;

  /**
   * 入职时间
   */
  @Column(name = "entry_date")
  private String entryDate;

  /**
   * 毕业院校
   */
  @Column(name = "graduated_school")
  private String graduatedSchool;

  /**
   * 学历
   */
  private Integer education;

  /**
   * 籍贯
   */
  private String origin;

  /**
   * 省
   */
  private String province;

  /**
   * 市
   */
  private String city;

  /**
   * 区/县/自治州
   */
  private String country;

  /**
   * 是否有员工折扣
   */
  private Boolean discount;

  /**
   * 离职日期
   */
  @Column(name = "leave_time")
  private String leaveTime;

  /**
   * 职称
   */
  private String title;

  /**
   * 奖金系数
   */
  @Column(name = "bonus_coefficient")
  private Double bonusCoefficient;

  /**
   * 基本工作量
   */
  @Column(name = "work_amount")
  private Double workAmount;

  /**
   * 职级
   */
  @Column(name = "post_level")
  private String postLevel;

  /**
   * 紧急联系人姓名
   */
  @Column(name = "emergency_contact")
  private String emergencyContact;

  /**
   * 紧急联系人联系人电话
   */
  @Column(name = "emergency_contact_phone")
  private String emergencyContactPhone;

  /**
   * 个人照片链接
   */
  private String photo;

  /**
   * 毕业证书照片:多张照片用;隔开
   */
  @Column(name = "diploma_photo")
  private String diplomaPhoto;

  /**
   * 工号
   */
  @Column(name = "work_number")
  private String workNumber;

  /**
   * 人员类别
   */
  private String category;

  /**
   * 户口性质
   */
  private String registration;

  /**
   * 户籍地址
   */
  @Column(name = "permanent_address")
  private String permanentAddress;

  /**
   *  毕业时间
   */
  @Column(name = "graduation_time")
  private String graduationTime;

  /**
   *  学习专业
   */
  @Column(name = "study_major")
  private String studyMajor;

  /**
   * 上一份工作单位
   */
  @Column(name = "last_job")
  private String lastJob;

  /**
   * 试用期结束时间
   */
  @Column(name = "probation_period_time")
  private String probationPeriodTime;

  /**
   * 创建人ID
   */
  @Column(name = "crt_id")
  private Integer crtId;

  /**
   * 创建人名称
   */
  @Column(name = "crt_name")
  private String crtName;

  /**
   * 创建时间
   */
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @Column(name = "crt_time")
  private Date crtTime;

  /**
   * 修改人ID
   */
  @Column(name = "upd_id")
  private Integer updId;

  /**
   * 修改人名称
   */
  @Column(name = "upd_name")
  private String updName;

  /**
   * 修改时间
   */
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @Column(name = "upd_time")
  private Date updTime;

  /**
   * @return id
   */
  public Integer getId() {
    return id;
  }

  /**
   * @param id
   */
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
   * 获取姓名拼音首字母缩写
   *
   * @return pinyin - 姓名拼音首字母缩写
   */
  public String getPinyin() {
    return pinyin;
  }

  /**
   * 设置姓名拼音首字母缩写
   *
   * @param pinyin 姓名拼音首字母缩写
   */
  public void setPinyin(String pinyin) {
    this.pinyin = pinyin;
  }

  /**
   * 获取性别: 0, 男; 1, 女
   *
   * @return gender - 性别: 0, 男; 1, 女
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
   * @return telephone - 固定电话
   */
  public String getTelephone() {
    return telephone;
  }

  /**
   * 设置固定电话
   *
   * @param telephone 固定电话
   */
  public void setTelephone(String telephone) {
    this.telephone = telephone;
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
   * 获取生日
   *
   * @return birthday - 生日
   */
  public String getBirthday() {
    return birthday;
  }

  /**
   * 设置生日
   *
   * @param birthday 生日
   */
  public void setBirthday(String birthday) {
    this.birthday = birthday;
  }

  /**
   * 获取居住地址-家庭地址
   *
   * @return address - 居住地址-家庭地址
   */
  public String getAddress() {
    return address;
  }

  /**
   * 设置居住地址-家庭地址
   *
   * @param address 居住地址-家庭地址
   */
  public void setAddress(String address) {
    this.address = address;
  }

  /**
   * 获取试用: 0, 正式: 1， 离职:2；实习: 3；
   *
   * @return work_status - 试用: 0, 正式: 1， 离职:2；实习: 3；
   */
  public Byte getWorkStatus() {
    return workStatus;
  }

  /**
   * 设置试用: 0, 正式: 1， 离职:2；实习: 3；
   *
   * @param workStatus 试用: 0, 正式: 1， 离职:2；实习: 3；
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
  public String getContractSigningDate() {
    return contractSigningDate;
  }

  /**
   * 设置合同签署日期
   *
   * @param contractSigningDate 合同签署日期
   */
  public void setContractSigningDate(String contractSigningDate) {
    this.contractSigningDate = contractSigningDate;
  }

  /**
   * 获取入职时间
   *
   * @return entry_date - 入职时间
   */
  public String getEntryDate() {
    return entryDate;
  }

  /**
   * 设置入职时间
   *
   * @param entryDate 入职时间
   */
  public void setEntryDate(String entryDate) {
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
   * 获取籍贯
   *
   * @return origin - 籍贯
   */
  public String getOrigin() {
    return origin;
  }

  /**
   * 设置籍贯
   *
   * @param origin 籍贯
   */
  public void setOrigin(String origin) {
    this.origin = origin;
  }

  /**
   * 获取省
   *
   * @return province - 省
   */
  public String getProvince() {
    return province;
  }

  /**
   * 设置省
   *
   * @param province 省
   */
  public void setProvince(String province) {
    this.province = province;
  }

  /**
   * 获取市
   *
   * @return city - 市
   */
  public String getCity() {
    return city;
  }

  /**
   * 设置市
   *
   * @param city 市
   */
  public void setCity(String city) {
    this.city = city;
  }

  /**
   * 获取区/县/自治州
   *
   * @return country - 区/县/自治州
   */
  public String getCountry() {
    return country;
  }

  /**
   * 设置区/县/自治州
   *
   * @param country 区/县/自治州
   */
  public void setCountry(String country) {
    this.country = country;
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
   * 获取离职日期
   *
   * @return leave_time - 离职日期
   */
  public String getLeaveTime() {
    return leaveTime;
  }

  /**
   * 设置离职日期
   *
   * @param leaveTime 离职日期
   */
  public void setLeaveTime(String leaveTime) {
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
   * 获取奖金系数
   *
   * @return bonus_coefficient - 奖金系数
   */
  public Double getBonusCoefficient() {
    return bonusCoefficient;
  }

  /**
   * 设置奖金系数
   *
   * @param bonusCoefficient 奖金系数
   */
  public void setBonusCoefficient(Double bonusCoefficient) {
    this.bonusCoefficient = bonusCoefficient;
  }

  /**
   * 获取基本工作量
   *
   * @return work_amount - 基本工作量
   */
  public Double getWorkAmount() {
    return workAmount;
  }

  /**
   * 设置基本工作量
   *
   * @param workAmount 基本工作量
   */
  public void setWorkAmount(Double workAmount) {
    this.workAmount = workAmount;
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
   * 获取人员类别
   *
   * @return category - 人员类别
   */
  public String getCategory() {
    return category;
  }

  /**
   * 设置人员类别
   *
   * @param category 人员类别
   */
  public void setCategory(String category) {
    this.category = category;
  }

  /**
   * 获取户口性质
   *
   * @return registration - 户口性质
   */
  public String getRegistration() {
    return registration;
  }

  /**
   * 设置户口性质
   *
   * @param registration 户口性质
   */
  public void setRegistration(String registration) {
    this.registration = registration;
  }

  /**
   * 获取户籍地址
   *
   * @return permanent_address - 户籍地址
   */
  public String getPermanentAddress() {
    return permanentAddress;
  }

  /**
   * 设置户籍地址
   *
   * @param permanentAddress 户籍地址
   */
  public void setPermanentAddress(String permanentAddress) {
    this.permanentAddress = permanentAddress;
  }

  /**
   * 获取 毕业时间
   *
   * @return graduation_time -  毕业时间
   */
  public String getGraduationTime() {
    return graduationTime;
  }

  /**
   * 设置 毕业时间
   *
   * @param graduationTime  毕业时间
   */
  public void setGraduationTime(String graduationTime) {
    this.graduationTime = graduationTime;
  }

  /**
   * 获取 学习专业
   *
   * @return study_major -  学习专业
   */
  public String getStudyMajor() {
    return studyMajor;
  }

  /**
   * 设置 学习专业
   *
   * @param studyMajor  学习专业
   */
  public void setStudyMajor(String studyMajor) {
    this.studyMajor = studyMajor;
  }

  /**
   * 获取上一份工作单位
   *
   * @return last_job - 上一份工作单位
   */
  public String getLastJob() {
    return lastJob;
  }

  /**
   * 设置上一份工作单位
   *
   * @param lastJob 上一份工作单位
   */
  public void setLastJob(String lastJob) {
    this.lastJob = lastJob;
  }

  /**
   * 获取试用期结束时间
   *
   * @return probation_period_time - 试用期结束时间
   */
  public String getProbationPeriodTime() {
    return probationPeriodTime;
  }

  /**
   * 设置试用期结束时间
   *
   * @param probationPeriodTime 试用期结束时间
   */
  public void setProbationPeriodTime(String probationPeriodTime) {
    this.probationPeriodTime = probationPeriodTime;
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

  /**
   * 获取修改人ID
   *
   * @return upd_id - 修改人ID
   */
  public Integer getUpdId() {
    return updId;
  }

  /**
   * 设置修改人ID
   *
   * @param updId 修改人ID
   */
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