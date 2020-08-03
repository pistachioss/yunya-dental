package com.yunya.models.patient_central;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Table(name = "patient_base_info")
public class PatientBaseInfo {
    /**
     * 主键
     */
    @Id
    @NotNull(message = "ID为空！")
    private Integer id;

    /**
     * 诊所ID 添加患者的组织ID
     */
    @Column(name = "org_id")
    @NotNull(message = "诊所ID为空！")
    private Integer orgId;

    /**
     * 患者姓名 字符串，长度64
     */
    @NotNull(message = "患者名称为空！")
    private String name;

    /**
     * 拼音姓名 字符串，长度64
     */
    @Column(name = "pinyin_name")
    private String pinyinName;

    /**
     * 头像地址 患者头像存储路径
     */
    @Column(name = "avatar_path")
    private String avatarPath;

    /**
     * 手机号码 长度14
     */
    @NotNull(message = "手机号码为空！")
    private String mobile;

    /**
     * 手机号所属人 手机号所属人字典ID
     */
    @Column(name = "mobile_owner")
    @NotNull(message = "手机号所属人为空！")
    private Integer mobileOwner;

    /**
     * 病历号 患者第一次就诊时生成
     */
    @Column(name = "medical_nummber")
    private String medicalNummber;

    /**
     * 性别 0-男；1-女；2-未知
     */
    private Byte gender;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 出生日期
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date birthday;

    /**
     * 患者来源类型 患者来源分类ID
     */
    @Column(name = "origin_type")
    private Integer originType;

    /**
     * 患者来源关联ID 患者来源关联ID（员工ID/患者ID/活动ID）
     */
    @Column(name = "origin_id")
    @NotNull(message = "患者来源关联ID为空！")
    private Integer originId;

    /**
     * 备注 备注
     */
    private String remarks;

    /**
     * 是否有效 是否有效
     */
    private Boolean inservice;

    /**
     * 创建人ID
     */
    @Column(name = "crt_id")
    private Integer crtId;

    /**
     * 创建人姓名
     */
    @Column(name = "crt_name")
    private String crtName;

    /**
     * 创建时间
     */
    @Column(name = "crt_time")
    private Date crtTime;

    /**
     * 更新人ID
     */
    @Column(name = "upt_id")
    private Integer uptId;

    /**
     * 更新人姓名
     */
    @Column(name = "upd_name")
    private String updName;

    /**
     * 更新时间
     */
    @Column(name = "upd_time")
    private Date updTime;

    /**
     * 获取主键
     *
     * @return id - 主键
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置主键
     *
     * @param id 主键
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取诊所ID 添加患者的组织ID
     *
     * @return org_id - 诊所ID 添加患者的组织ID
     */
    public Integer getOrgId() {
        return orgId;
    }

    /**
     * 设置诊所ID 添加患者的组织ID
     *
     * @param orgId 诊所ID 添加患者的组织ID
     */
    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    /**
     * 获取患者姓名 字符串，长度64
     *
     * @return name - 患者姓名 字符串，长度64
     */
    public String getName() {
        return name;
    }

    /**
     * 设置患者姓名 字符串，长度64
     *
     * @param name 患者姓名 字符串，长度64
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取拼音姓名 字符串，长度64
     *
     * @return pinyin_name - 拼音姓名 字符串，长度64
     */
    public String getPinyinName() {
        return pinyinName;
    }

    /**
     * 设置拼音姓名 字符串，长度64
     *
     * @param pinyinName 拼音姓名 字符串，长度64
     */
    public void setPinyinName(String pinyinName) {
        this.pinyinName = pinyinName;
    }

    /**
     * 获取头像地址 患者头像存储路径
     *
     * @return avatar_path - 头像地址 患者头像存储路径
     */
    public String getAvatarPath() {
        return avatarPath;
    }

    /**
     * 设置头像地址 患者头像存储路径
     *
     * @param avatarPath 头像地址 患者头像存储路径
     */
    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }

    /**
     * 获取手机号码 长度14
     *
     * @return mobile - 手机号码 长度14
     */
    public String getMobile() {
        return mobile;
    }

    /**
     * 设置手机号码 长度14
     *
     * @param mobile 手机号码 长度14
     */
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    /**
     * 获取手机号所属人 手机号所属人字典ID
     *
     * @return mobile_owner - 手机号所属人 手机号所属人字典ID
     */
    public Integer getMobileOwner() {
        return mobileOwner;
    }

    /**
     * 设置手机号所属人 手机号所属人字典ID
     *
     * @param mobileOwner 手机号所属人 手机号所属人字典ID
     */
    public void setMobileOwner(Integer mobileOwner) {
        this.mobileOwner = mobileOwner;
    }

    /**
     * 获取病历号 患者第一次就诊时生成
     *
     * @return medical_nummber - 病历号 患者第一次就诊时生成
     */
    public String getMedicalNummber() {
        return medicalNummber;
    }

    /**
     * 设置病历号 患者第一次就诊时生成
     *
     * @param medicalNummber 病历号 患者第一次就诊时生成
     */
    public void setMedicalNummber(String medicalNummber) {
        this.medicalNummber = medicalNummber;
    }

    /**
     * 获取性别 0-男；1-女；2-未知
     *
     * @return gender - 性别 0-男；1-女；2-未知
     */
    public Byte getGender() {
        return gender;
    }

    /**
     * 设置性别 0-男；1-女；2-未知
     *
     * @param gender 性别 0-男；1-女；2-未知
     */
    public void setGender(Byte gender) {
        this.gender = gender;
    }

    /**
     * 获取年龄
     *
     * @return age - 年龄
     */
    public Integer getAge() {
        return age;
    }

    /**
     * 设置年龄
     *
     * @param age 年龄
     */
    public void setAge(Integer age) {
        this.age = age;
    }

    /**
     * 获取出生日期
     *
     * @return birthday - 出生日期
     */
    public Date getBirthday() {
        return birthday;
    }

    /**
     * 设置出生日期
     *
     * @param birthday 出生日期
     */
    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    /**
     * 获取患者来源类型 患者来源分类ID
     *
     * @return origin_type - 患者来源类型 患者来源分类ID
     */
    public Integer getOriginType() {
        return originType;
    }

    /**
     * 设置患者来源类型 患者来源分类ID
     *
     * @param originType 患者来源类型 患者来源分类ID
     */
    public void setOriginType(Integer originType) {
        this.originType = originType;
    }

    /**
     * 获取患者来源关联ID 患者来源关联ID（员工ID/患者ID/活动ID）
     *
     * @return origin_id - 患者来源关联ID 患者来源关联ID（员工ID/患者ID/活动ID）
     */
    public Integer getOriginId() {
        return originId;
    }

    /**
     * 设置患者来源关联ID 患者来源关联ID（员工ID/患者ID/活动ID）
     *
     * @param originId 患者来源关联ID 患者来源关联ID（员工ID/患者ID/活动ID）
     */
    public void setOriginId(Integer originId) {
        this.originId = originId;
    }

    /**
     * 获取备注 备注
     *
     * @return remarks - 备注 备注
     */
    public String getRemarks() {
        return remarks;
    }

    /**
     * 设置备注 备注
     *
     * @param remarks 备注 备注
     */
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    /**
     * 获取是否有效 是否有效
     *
     * @return inservice - 是否有效 是否有效
     */
    public Boolean getInservice() {
        return inservice;
    }

    /**
     * 设置是否有效 是否有效
     *
     * @param inservice 是否有效 是否有效
     */
    public void setInservice(Boolean inservice) {
        this.inservice = inservice;
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
     * 获取创建人姓名
     *
     * @return crt_name - 创建人姓名
     */
    public String getCrtName() {
        return crtName;
    }

    /**
     * 设置创建人姓名
     *
     * @param crtName 创建人姓名
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
     * 获取更新人ID
     *
     * @return upt_id - 更新人ID
     */
    public Integer getUptId() {
        return uptId;
    }

    /**
     * 设置更新人ID
     *
     * @param uptId 更新人ID
     */
    public void setUptId(Integer uptId) {
        this.uptId = uptId;
    }

    /**
     * 获取更新人姓名
     *
     * @return upd_name - 更新人姓名
     */
    public String getUpdName() {
        return updName;
    }

    /**
     * 设置更新人姓名
     *
     * @param updName 更新人姓名
     */
    public void setUpdName(String updName) {
        this.updName = updName;
    }

    /**
     * 获取更新时间
     *
     * @return upd_time - 更新时间
     */
    public Date getUpdTime() {
        return updTime;
    }

    /**
     * 设置更新时间
     *
     * @param updTime 更新时间
     */
    public void setUpdTime(Date updTime) {
        this.updTime = updTime;
    }
}