package com.yunya.models.discount;

import lombok.*;

import javax.persistence.*;
import java.io.*;
import java.time.*;

/**
 * @author 
 * 优惠卡
 */
@Setter
@Getter
@Table(name = "card")
public class Card implements Serializable {

    @Id
    @GeneratedValue(generator = "JDBC", strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 组织id
     */
    @Column(name = "org_id")
    private Integer orgId;

    /**
     * 卡券Id
     */
    @Column(name = "coupon_id")
    private Integer couponId;

    /**
     * 优惠券分配id
     */
    @Column(name = "coupon_allocate_id")
    private Integer couponAllocateId;

    /**
     * 激活的组织Id
     */
    @Column(name = "active_org_id")
    private Integer activeOrgId;

    /**
     * 患者id(激活卡券)
     */
    @Column(name = "patient_id")
    private Integer patientId;

    /**
     * 激活人
     */
    @Column(name = "active_user_id")
    private Integer activeUserId;

    /**
     * 卡号
     */
    @Column(name = "card_number")
    private String cardNumber;

    /**
     * 卡密
     */
    @Column(name = "card_password")
    private String cardPassword;

    /**
     * 状态 0:待售出 1:待激活 2:已激活
     */
    private Integer status;

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
     * 售出类型 0:售出 1:置换 2:赠送
     */
    @Column(name = "sold_type")
    private Integer soldType;

    /**
     * 是否发送短信 0:否 1:是
     */
    @Column(name = "is_send_text")
    private Integer sendText;

    /**
     * 是否售出并收款 0:否 1:是
     */
    @Column(name = "is_sold_and_pay")
    private Integer soldAndPay;

    /**
     * 入账方式
     */
    @Column(name = "pay_id")
    private Integer payId;

    /**
     * 共享人
     */
    private String sharer;

    /**
     * 销售渠道
     */
    @Column(name = "sale_channel_id")
    private Integer saleChannelId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 链接
     */
    private String link;

    /**
     * 售出方式 0:线上 1:线下
     */
    @Column(name = "sold_way")
    private Integer soldWay;

    /**
     * 是否支付 0:未支付 1:已支付
     */
    @Column(name = "is_pay")
    private Integer pay;

    /**
     * 售出日期
     */
    @Column(name = "sold_date")
    private LocalDateTime soldDate;

    /**
     * 激活时间
     */
    @Column(name = "active_date")
    private LocalDateTime activeDate;

    /**
     * 创建人
     */
    @Column(name = "crt_id")
    private Integer crtId;

    /**
     * 售出人
     */
    @Column(name = "seller_user_id")
    private Integer sellerUserId;

    /**
     * 创建时间
     */
    @Column(name = "crt_time")
    private LocalDateTime crtTime;

    /**
     * 更新人
     */
    @Column(name = "upd_id")
    private Integer updId;

    /**
     * 更新时间
     */
    @Column(name = "upd_time")
    private LocalDateTime updTime;
}