package com.yunya.models.discount;

import lombok.*;

import javax.persistence.*;
import java.time.*;

/**
 * @author 
 * 优惠卡
 */
@Setter
@Getter
@Table(name = "card")
public class Card {

    @Id
    @GeneratedValue(generator = "JDBC")
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
     * 激活的组织Id
     */
    @Column(name = "active_org_id")
    private Integer activeOrgId;

    /**
     * 系统患者id（以后c端用）
     */
    @Column(name = "patient_id")
    private Integer patientId;

    /**
     * 卡号
     */
    @Column(name = "card_number")
    private String cardNumber;

    /**
     * 卡密
     */
    private String password;

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
    @Column(name = "soldAndPay")
    private Integer soldAndPay;

    /**
     * 入账方式分类
     */
    @Column(name = "pay_type_id")
    private Integer payTypeId;

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
     * 创建人
     */
    @Column(name = "crt_id")
    private Integer crtId;

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