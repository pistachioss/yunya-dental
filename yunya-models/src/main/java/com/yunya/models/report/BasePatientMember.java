package com.yunya.models.report;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;

@Table(name = "base_patient_member")
public class BasePatientMember {
    /**
     * 卡ID
     */
    @Column(name = "card_id")
    private Integer cardId;

    /**
     * 卡号
     */
    @Id
    @Column(name = "card_number")
    private String cardNumber;

    /**
     * 卡类型(0：会员卡；１：预付款)
     */
    private Integer type;

    /**
     * 会员卡级别(金藤卡，银藤卡)
     */
    @Column(name = "member_level_id")
    private Integer memberLevelId;

    /**
     * 本金
     */
    @Column(name = "principal_amount")
    private BigDecimal principalAmount;

    /**
     * 赠金
     */
    @Column(name = "bonus_amount")
    private BigDecimal bonusAmount;

    /**
     * 患者ID
     */
    @Column(name = "patient_id")
    private Integer patientId;

    /**
     * 开卡日期
     */
    @Column(name = "card_opening_date")
    private Date cardOpeningDate;

    /**
     * 获取卡ID
     *
     * @return card_id - 卡ID
     */
    public Integer getCardId() {
        return cardId;
    }

    /**
     * 设置卡ID
     *
     * @param cardId 卡ID
     */
    public void setCardId(Integer cardId) {
        this.cardId = cardId;
    }

    /**
     * 获取卡号
     *
     * @return card_number - 卡号
     */
    public String getCardNumber() {
        return cardNumber;
    }

    /**
     * 设置卡号
     *
     * @param cardNumber 卡号
     */
    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    /**
     * 获取卡类型(0：会员卡；１：预付款)
     *
     * @return type - 卡类型(0：会员卡；１：预付款)
     */
    public Integer getType() {
        return type;
    }

    /**
     * 设置卡类型(0：会员卡；１：预付款)
     *
     * @param type 卡类型(0：会员卡；１：预付款)
     */
    public void setType(Integer type) {
        this.type = type;
    }

    /**
     * 获取会员卡级别(金藤卡，银藤卡)
     *
     * @return member_level_id - 会员卡级别(金藤卡，银藤卡)
     */
    public Integer getMemberLevelId() {
        return memberLevelId;
    }

    /**
     * 设置会员卡级别(金藤卡，银藤卡)
     *
     * @param memberLevelId 会员卡级别(金藤卡，银藤卡)
     */
    public void setMemberLevelId(Integer memberLevelId) {
        this.memberLevelId = memberLevelId;
    }

    /**
     * 获取本金
     *
     * @return principal_amount - 本金
     */
    public BigDecimal getPrincipalAmount() {
        return principalAmount;
    }

    /**
     * 设置本金
     *
     * @param principalAmount 本金
     */
    public void setPrincipalAmount(BigDecimal principalAmount) {
        this.principalAmount = principalAmount;
    }

    /**
     * 获取赠金
     *
     * @return bonus_amount - 赠金
     */
    public BigDecimal getBonusAmount() {
        return bonusAmount;
    }

    /**
     * 设置赠金
     *
     * @param bonusAmount 赠金
     */
    public void setBonusAmount(BigDecimal bonusAmount) {
        this.bonusAmount = bonusAmount;
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
     * 获取开卡日期
     *
     * @return card_opening_date - 开卡日期
     */
    public Date getCardOpeningDate() {
        return cardOpeningDate;
    }

    /**
     * 设置开卡日期
     *
     * @param cardOpeningDate 开卡日期
     */
    public void setCardOpeningDate(Date cardOpeningDate) {
        this.cardOpeningDate = cardOpeningDate;
    }
}