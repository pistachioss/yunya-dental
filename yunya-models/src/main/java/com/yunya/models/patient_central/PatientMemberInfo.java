package com.yunya.models.patient_central;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;

@Table(name = "patient_member_info")
public class PatientMemberInfo {
    /**
     * 主键
     */
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    /**
     * 诊所ID
     */
    @Column(name = "org_id")
    private Integer orgId;

    /**
     * 患者ID
     */
    @Column(name = "patient_id")
    private Integer patientId;

    /**
     * 会员卡类型ID
     */
    @Column(name = "member_type_id")
    private Integer memberTypeId;

    /**
     * 是否主卡 只有老会员为主卡（青藤、银滕、金藤）
     */
    @Column(name = "master_card")
    private Boolean masterCard;

    /**
     * 会员卡卡号
     */
    @Column(name = "card_number")
    private String cardNumber;

    /**
     * 会员卡状态 0：正常，1：锁定；2：退卡
     */
    private Byte status;

    /**
     * 会员卡本金 充值金额
     */
    @Column(name = "principal_amount")
    private BigDecimal principalAmount;

    /**
     * 会员卡赠金 充值赠送金额
     */
    @Column(name = "bonus_amount")
    private BigDecimal bonusAmount;

    /**
     * 会员卡积分
     */
    private Integer point;

    /**
     * 会员卡续费前到期时间
     */
    @Column(name = "last_expired_time")
    private Date lastExpiredTime;

    /**
     * 会员卡续费后到期时间
     */
    @Column(name = "next_expired_time")
    private Date nextExpiredTime;

    /**
     * 开始计算累计充值的时间
     */
    @Column(name = "calc_amount_time")
    private Date calcAmountTime;

    /**
     * 备注 备注
     */
    private String remarks;

    /**
     * 是否启用 是否有效
     */
    private Boolean inservice;

    /**
     * 不自动升级
     */
    private Boolean nonauto;

    /**
     * 可降级的最低等级会员ID
     */
    @Column(name = "min_type_id")
    private Integer minTypeId;

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
     * 创建时间 可当作开卡时间
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
     * 获取患者ID
     *
     * @return patient_id - 患者ID
     */
    public Integer getPatientId() {
        return patientId;
    }

    /**
     * 设置患者ID
     *
     * @param patientId 患者ID
     */
    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    /**
     * 获取会员卡类型ID
     *
     * @return member_type_id - 会员卡类型ID
     */
    public Integer getMemberTypeId() {
        return memberTypeId;
    }

    /**
     * 设置会员卡类型ID
     *
     * @param memberTypeId 会员卡类型ID
     */
    public void setMemberTypeId(Integer memberTypeId) {
        this.memberTypeId = memberTypeId;
    }

    /**
     * 获取是否主卡 只有老会员为主卡（青藤、银滕、金藤）
     *
     * @return master_card - 是否主卡 只有老会员为主卡（青藤、银滕、金藤）
     */
    public Boolean getMasterCard() {
        return masterCard;
    }

    /**
     * 设置是否主卡 只有老会员为主卡（青藤、银滕、金藤）
     *
     * @param masterCard 是否主卡 只有老会员为主卡（青藤、银滕、金藤）
     */
    public void setMasterCard(Boolean masterCard) {
        this.masterCard = masterCard;
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
     * 获取会员卡状态 0：正常，1：锁定；2：退卡
     *
     * @return status - 会员卡状态 0：正常，1：锁定；2：退卡
     */
    public Byte getStatus() {
        return status;
    }

    /**
     * 设置会员卡状态 0：正常，1：锁定；2：退卡
     *
     * @param status 会员卡状态 0：正常，1：锁定；2：退卡
     */
    public void setStatus(Byte status) {
        this.status = status;
    }

    /**
     * 获取会员卡本金 充值金额
     *
     * @return principal_amount - 会员卡本金 充值金额
     */
    public BigDecimal getPrincipalAmount() {
        return principalAmount;
    }

    /**
     * 设置会员卡本金 充值金额
     *
     * @param principalAmount 会员卡本金 充值金额
     */
    public void setPrincipalAmount(BigDecimal principalAmount) {
        this.principalAmount = principalAmount;
    }

    /**
     * 获取会员卡赠金 充值赠送金额
     *
     * @return bonus_amount - 会员卡赠金 充值赠送金额
     */
    public BigDecimal getBonusAmount() {
        return bonusAmount;
    }

    /**
     * 设置会员卡赠金 充值赠送金额
     *
     * @param bonusAmount 会员卡赠金 充值赠送金额
     */
    public void setBonusAmount(BigDecimal bonusAmount) {
        this.bonusAmount = bonusAmount;
    }

    /**
     * 获取会员卡积分
     *
     * @return point - 会员卡积分
     */
    public Integer getPoint() {
        return point;
    }

    /**
     * 设置会员卡积分
     *
     * @param point 会员卡积分
     */
    public void setPoint(Integer point) {
        this.point = point;
    }

    /**
     * 获取会员卡续费前到期时间
     *
     * @return last_expired_time - 会员卡续费前到期时间
     */
    public Date getLastExpiredTime() {
        return lastExpiredTime;
    }

    /**
     * 设置会员卡续费前到期时间
     *
     * @param lastExpiredTime 会员卡续费前到期时间
     */
    public void setLastExpiredTime(Date lastExpiredTime) {
        this.lastExpiredTime = lastExpiredTime;
    }

    /**
     * 获取会员卡续费后到期时间
     *
     * @return next_expired_time - 会员卡续费后到期时间
     */
    public Date getNextExpiredTime() {
        return nextExpiredTime;
    }

    /**
     * 设置会员卡续费后到期时间
     *
     * @param nextExpiredTime 会员卡续费后到期时间
     */
    public void setNextExpiredTime(Date nextExpiredTime) {
        this.nextExpiredTime = nextExpiredTime;
    }

    public Date getCalcAmountTime() {
        return calcAmountTime;
    }

    public void setCalcAmountTime(Date calcAmountTime) {
        this.calcAmountTime = calcAmountTime;
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

    public Boolean getNonauto() {
        return nonauto;
    }

    public void setNonauto(Boolean nonauto) {
        this.nonauto = nonauto;
    }

    public Integer getMinTypeId() {
        return minTypeId;
    }

    public void setMinTypeId(Integer minTypeId) {
        this.minTypeId = minTypeId;
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
     * 获取创建时间 可当作开卡时间
     *
     * @return crt_time - 创建时间 可当作开卡时间
     */
    public Date getCrtTime() {
        return crtTime;
    }

    /**
     * 设置创建时间 可当作开卡时间
     *
     * @param crtTime 创建时间 可当作开卡时间
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