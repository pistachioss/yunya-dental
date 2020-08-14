package com.yunya.models.patient_central;

import java.util.Date;
import javax.persistence.*;

@Table(name = "patient_member_change_log")
public class PatientMemberChangeLog {
    /**
     * 主键
     */
    @Id
    private Integer id;

    /**
     * 会员卡卡号
     */
    @Column(name = "card_number")
    private String cardNumber;

    /**
     * 会员卡类型名称
     */
    @Column(name = "member_card_name")
    private String memberCardName;

    /**
     * 会员卡类型id
     */
    @Column(name = "member_type_id")
    private Integer memberTypeId;

    /**
     * 诊所ID
     */
    @Column(name = "org_id")
    private Integer orgId;

    /**
     * 操作门诊名称
     */
    @Column(name = "org_name")
    private String orgName;

    /**
     * 操作类型
     */
    private String operationType;

    /**
     * 操作人id
     */
    @Column(name = "operator_id")
    private Integer operatorId;

    /**
     * 操作人姓名
     */
    @Column(name = "operator_name")
    private String operatorName;

    /**
     * 操作时间
     */
    @Column(name = "operating_time")
    private Date operatingTime;

    /**
     * 是否启用 是否有效
     */
    private Boolean inservice;

    /**
     * 创建人id
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
     * 更新人id
     */
    @Column(name = "upt_id")
    private Integer uptId;

    /**
     * 更新人姓名
     */
    @Column(name = "upt_name")
    private String uptName;

    /**
     * 更新时间
     */
    @Column(name = "upt_time")
    private Date uptTime;

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
     * 获取会员卡卡号
     *
     * @return card_number - 会员卡卡号
     */
    public String getCardNumber() {
        return cardNumber;
    }

    /**
     * 设置会员卡卡号
     *
     * @param cardNumber 会员卡卡号
     */
    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    /**
     * 获取会员卡类型名称
     *
     * @return member_card_name - 会员卡类型名称
     */
    public String getMemberCardName() {
        return memberCardName;
    }

    /**
     * 设置会员卡类型名称
     *
     * @param memberCardName 会员卡类型名称
     */
    public void setMemberCardName(String memberCardName) {
        this.memberCardName = memberCardName;
    }

    /**
     * 获取会员卡类型id
     *
     * @return member_type_id - 会员卡类型id
     */
    public Integer getMemberTypeId() {
        return memberTypeId;
    }

    /**
     * 设置会员卡类型id
     *
     * @param memberTypeId 会员卡类型id
     */
    public void setMemberTypeId(Integer memberTypeId) {
        this.memberTypeId = memberTypeId;
    }

    /**
     * 获取诊所ID
     *
     * @return org_id - 诊所ID
     */
    public Integer getOrgId() {
        return orgId;
    }

    /**
     * 设置诊所ID
     *
     * @param orgId 诊所ID
     */
    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    /**
     * 获取操作门诊名称
     *
     * @return org_name - 操作门诊名称
     */
    public String getOrgName() {
        return orgName;
    }

    /**
     * 设置操作门诊名称
     *
     * @param orgName 操作门诊名称
     */
    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    /**
     * 获取操作人id
     *
     * @return operator_id - 操作人id
     */
    public Integer getOperatorId() {
        return operatorId;
    }

    /**
     * 设置操作人id
     *
     * @param operatorId 操作人id
     */
    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    /**
     * 获取操作人姓名
     *
     * @return operator_name - 操作人姓名
     */
    public String getOperatorName() {
        return operatorName;
    }

    /**
     * 设置操作人姓名
     *
     * @param operatorName 操作人姓名
     */
    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    /**
     * 获取操作时间
     *
     * @return operating_time - 操作时间
     */
    public Date getOperatingTime() {
        return operatingTime;
    }

    /**
     * 设置操作时间
     *
     * @param operatingTime 操作时间
     */
    public void setOperatingTime(Date operatingTime) {
        this.operatingTime = operatingTime;
    }

    /**
     * 获取是否启用 是否有效
     *
     * @return inservice - 是否启用 是否有效
     */
    public Boolean getInservice() {
        return inservice;
    }

    /**
     * 设置是否启用 是否有效
     *
     * @param inservice 是否启用 是否有效
     */
    public void setInservice(Boolean inservice) {
        this.inservice = inservice;
    }

    /**
     * 获取创建人id
     *
     * @return crt_id - 创建人id
     */
    public Integer getCrtId() {
        return crtId;
    }

    /**
     * 设置创建人id
     *
     * @param crtId 创建人id
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
     * 获取更新人id
     *
     * @return upt_id - 更新人id
     */
    public Integer getUptId() {
        return uptId;
    }

    /**
     * 设置更新人id
     *
     * @param uptId 更新人id
     */
    public void setUptId(Integer uptId) {
        this.uptId = uptId;
    }

    /**
     * 获取更新人姓名
     *
     * @return upt_name - 更新人姓名
     */
    public String getUptName() {
        return uptName;
    }

    /**
     * 设置更新人姓名
     *
     * @param uptName 更新人姓名
     */
    public void setUptName(String uptName) {
        this.uptName = uptName;
    }

    /**
     * 获取更新时间
     *
     * @return upt_time - 更新时间
     */
    public Date getUptTime() {
        return uptTime;
    }

    /**
     * 设置更新时间
     *
     * @param uptTime 更新时间
     */
    public void setUptTime(Date uptTime) {
        this.uptTime = uptTime;
    }

    /**
     * 获取操作类型
     * @return operationType
     */
    public String getOperationType() {
        return operationType;
    }

    /**
     * 设置操作类型
     * @param operationType
     */
    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }
}