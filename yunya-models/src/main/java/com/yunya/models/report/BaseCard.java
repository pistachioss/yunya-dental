package com.yunya.models.report;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Data
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
    private LocalDateTime allocateDate;

    /**
     * 状态 (0-生成、1-售出、2-激活、3-部分使用、4-全部使用)
     */
    private Integer status;

    /**
     * 生成日期
     */
    @Column(name = "generate_date")
    private LocalDateTime generateDate;

    /**
     * 售出日期
     */
    @Column(name = "sold_date")
    private LocalDateTime soldDate;

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
    private Integer soldType;

    /**
     * 售出方式（0-线上 1-线下）
     */
    @Column(name = "sold_way")
    private Integer soldWay;

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
    private Integer chargeStatus;

    /**
     * 激活日期
     */
    @Column(name = "active_date")
    private LocalDateTime activeDate;

    /**
     * 使用有效期
     */
    @Column(name = "activation_deadline")
    private LocalDateTime activationDeadline;

    /**
     * 初次使用日期
     */
    @Column(name = "first_use_date")
    private LocalDateTime firstUseDate;

}