package com.yunya.models.middletable;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "base_card")
public class BaseCard {
    /**
     * 卡券ID
     */
    @Column(name = "card_id")
    private Integer cardId;

    /**
     * 产品ID
     */
    @Column(name = "coupon_id")
    private Integer couponId;

    /**
     * 卡号
     */
    @Column(name = "card_number")
    private String cardNumber;

    /**
     * 分配组织ID
     */
    @Column(name = "allocate_org_id")
    private Integer allocateOrgId;

    /**
     * 激活卡主ID
     */
    @Column(name = "patient_id")
    private Integer patientId;

    /**
     * 激活人
     */
    @Column(name = "active_user_id")
    private Integer activeUserId;

    /**
     * 激活门诊
     */
    @Column(name = "active_org_id")
    private Integer activeOrgId;

    /**
     * 售出人
     */
    @Column(name = "seller_user_id")
    private Integer sellerUserId;

    /**
     * 分配日期
     */
    @Column(name = "allocate_date")
    private Date allocateDate;

    /**
     * 状态 (0-生成、1-售出、2-激活、3-部分使用、4-全部使用)
     */
    private Byte status;

    /**
     * 生成日期
     */
    @Column(name = "generate_date")
    private Date generateDate;

    /**
     * 售出日期
     */
    @Column(name = "sold_date")
    private Date soldDate;

    /**
     * 售出对象
     */
    @Column(name = "sold_target")
    private String soldTarget;

    /**
     * 售出对象手机号
     */
    @Column(name = "sold_phone_number")
    private String soldPhoneNumber;

    /**
     * 售出类型（0-售出 1-置换 2-赠送）
     */
    @Column(name = "sold_type")
    private Byte soldType;

    /**
     * 售出方式（0-线上 1-线下）
     */
    @Column(name = "sold_way")
    private Byte soldWay;

    /**
     * 售出入账方式ID
     */
    @Column(name = "pay_id")
    private Integer payId;

    /**
     * 售出入账方式名称
     */
    @Column(name = "pay_name")
    private String payName;

    /**
     * 售出收费状态（0-否 1-是）
     */
    @Column(name = "charge_status")
    private Byte chargeStatus;

    /**
     * 激活日期
     */
    @Column(name = "active_date")
    private Date activeDate;

    /**
     * 使用有效期
     */
    @Column(name = "activation_deadline")
    private Date activationDeadline;

    /**
     * 初次使用日期
     */
    @Column(name = "first_use_date")
    private Date firstUseDate;

    /**
     * 获取卡券ID
     *
     * @return card_id - 卡券ID
     */
    public Integer getCardId() {
        return cardId;
    }

    /**
     * 设置卡券ID
     *
     * @param cardId 卡券ID
     */
    public void setCardId(Integer cardId) {
        this.cardId = cardId;
    }

    /**
     * 获取产品ID
     *
     * @return coupon_id - 产品ID
     */
    public Integer getCouponId() {
        return couponId;
    }

    /**
     * 设置产品ID
     *
     * @param couponId 产品ID
     */
    public void setCouponId(Integer couponId) {
        this.couponId = couponId;
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
     * 获取分配组织ID
     *
     * @return allocate_org_id - 分配组织ID
     */
    public Integer getAllocateOrgId() {
        return allocateOrgId;
    }

    /**
     * 设置分配组织ID
     *
     * @param allocateOrgId 分配组织ID
     */
    public void setAllocateOrgId(Integer allocateOrgId) {
        this.allocateOrgId = allocateOrgId;
    }

    /**
     * 获取激活卡主ID
     *
     * @return patient_id - 激活卡主ID
     */
    public Integer getPatientId() {
        return patientId;
    }

    /**
     * 设置激活卡主ID
     *
     * @param patientId 激活卡主ID
     */
    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    /**
     * 获取激活人
     *
     * @return active_user_id - 激活人
     */
    public Integer getActiveUserId() {
        return activeUserId;
    }

    /**
     * 设置激活人
     *
     * @param activeUserId 激活人
     */
    public void setActiveUserId(Integer activeUserId) {
        this.activeUserId = activeUserId;
    }

    /**
     * 获取激活门诊
     *
     * @return active_org_id - 激活门诊
     */
    public Integer getActiveOrgId() {
        return activeOrgId;
    }

    /**
     * 设置激活门诊
     *
     * @param activeOrgId 激活门诊
     */
    public void setActiveOrgId(Integer activeOrgId) {
        this.activeOrgId = activeOrgId;
    }

    /**
     * 获取售出人
     *
     * @return seller_user_id - 售出人
     */
    public Integer getSellerUserId() {
        return sellerUserId;
    }

    /**
     * 设置售出人
     *
     * @param sellerUserId 售出人
     */
    public void setSellerUserId(Integer sellerUserId) {
        this.sellerUserId = sellerUserId;
    }

    /**
     * 获取分配日期
     *
     * @return allocate_date - 分配日期
     */
    public Date getAllocateDate() {
        return allocateDate;
    }

    /**
     * 设置分配日期
     *
     * @param allocateDate 分配日期
     */
    public void setAllocateDate(Date allocateDate) {
        this.allocateDate = allocateDate;
    }

    /**
     * 获取状态 (0-生成、1-售出、2-激活、3-部分使用、4-全部使用)
     *
     * @return status - 状态 (0-生成、1-售出、2-激活、3-部分使用、4-全部使用)
     */
    public Byte getStatus() {
        return status;
    }

    /**
     * 设置状态 (0-生成、1-售出、2-激活、3-部分使用、4-全部使用)
     *
     * @param status 状态 (0-生成、1-售出、2-激活、3-部分使用、4-全部使用)
     */
    public void setStatus(Byte status) {
        this.status = status;
    }

    /**
     * 获取生成日期
     *
     * @return generate_date - 生成日期
     */
    public Date getGenerateDate() {
        return generateDate;
    }

    /**
     * 设置生成日期
     *
     * @param generateDate 生成日期
     */
    public void setGenerateDate(Date generateDate) {
        this.generateDate = generateDate;
    }

    /**
     * 获取售出日期
     *
     * @return sold_date - 售出日期
     */
    public Date getSoldDate() {
        return soldDate;
    }

    /**
     * 设置售出日期
     *
     * @param soldDate 售出日期
     */
    public void setSoldDate(Date soldDate) {
        this.soldDate = soldDate;
    }

    /**
     * 获取售出对象
     *
     * @return sold_target - 售出对象
     */
    public String getSoldTarget() {
        return soldTarget;
    }

    /**
     * 设置售出对象
     *
     * @param soldTarget 售出对象
     */
    public void setSoldTarget(String soldTarget) {
        this.soldTarget = soldTarget;
    }

    /**
     * 获取售出对象手机号
     *
     * @return sold_phone_number - 售出对象手机号
     */
    public String getSoldPhoneNumber() {
        return soldPhoneNumber;
    }

    /**
     * 设置售出对象手机号
     *
     * @param soldPhoneNumber 售出对象手机号
     */
    public void setSoldPhoneNumber(String soldPhoneNumber) {
        this.soldPhoneNumber = soldPhoneNumber;
    }

    /**
     * 获取售出类型（0-售出 1-置换 2-赠送）
     *
     * @return sold_type - 售出类型（0-售出 1-置换 2-赠送）
     */
    public Byte getSoldType() {
        return soldType;
    }

    /**
     * 设置售出类型（0-售出 1-置换 2-赠送）
     *
     * @param soldType 售出类型（0-售出 1-置换 2-赠送）
     */
    public void setSoldType(Byte soldType) {
        this.soldType = soldType;
    }

    /**
     * 获取售出方式（0-线上 1-线下）
     *
     * @return sold_way - 售出方式（0-线上 1-线下）
     */
    public Byte getSoldWay() {
        return soldWay;
    }

    /**
     * 设置售出方式（0-线上 1-线下）
     *
     * @param soldWay 售出方式（0-线上 1-线下）
     */
    public void setSoldWay(Byte soldWay) {
        this.soldWay = soldWay;
    }

    /**
     * 获取售出入账方式ID
     *
     * @return pay_id - 售出入账方式ID
     */
    public Integer getPayId() {
        return payId;
    }

    /**
     * 设置售出入账方式ID
     *
     * @param payId 售出入账方式ID
     */
    public void setPayId(Integer payId) {
        this.payId = payId;
    }

    /**
     * 获取售出入账方式名称
     *
     * @return pay_name - 售出入账方式名称
     */
    public String getPayName() {
        return payName;
    }

    /**
     * 设置售出入账方式名称
     *
     * @param payName 售出入账方式名称
     */
    public void setPayName(String payName) {
        this.payName = payName;
    }

    /**
     * 获取售出收费状态（0-否 1-是）
     *
     * @return charge_status - 售出收费状态（0-否 1-是）
     */
    public Byte getChargeStatus() {
        return chargeStatus;
    }

    /**
     * 设置售出收费状态（0-否 1-是）
     *
     * @param chargeStatus 售出收费状态（0-否 1-是）
     */
    public void setChargeStatus(Byte chargeStatus) {
        this.chargeStatus = chargeStatus;
    }

    /**
     * 获取激活日期
     *
     * @return active_date - 激活日期
     */
    public Date getActiveDate() {
        return activeDate;
    }

    /**
     * 设置激活日期
     *
     * @param activeDate 激活日期
     */
    public void setActiveDate(Date activeDate) {
        this.activeDate = activeDate;
    }

    /**
     * 获取使用有效期
     *
     * @return activation_deadline - 使用有效期
     */
    public Date getActivationDeadline() {
        return activationDeadline;
    }

    /**
     * 设置使用有效期
     *
     * @param activationDeadline 使用有效期
     */
    public void setActivationDeadline(Date activationDeadline) {
        this.activationDeadline = activationDeadline;
    }

    /**
     * 获取初次使用日期
     *
     * @return first_use_date - 初次使用日期
     */
    public Date getFirstUseDate() {
        return firstUseDate;
    }

    /**
     * 设置初次使用日期
     *
     * @param firstUseDate 初次使用日期
     */
    public void setFirstUseDate(Date firstUseDate) {
        this.firstUseDate = firstUseDate;
    }
}