package com.yunya.models.report;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Table(name = "base_coupon_order_virtual")
public class BaseCouponOrderVirtual {

    @Id
    @Column(name = "virtual_id")
    private Integer virtualId;
    /**
     * 订单id
     */
    @Column(name = "order_id")
    private Integer orderId;

    /**
     * 订单号
     */
    @Column(name = "order_sn")
    private String orderSn;

    /**
     * 卡券id
     */
    @Column(name = "card_id")
    private Integer cardId;

    /**
     * 礼包id
     */
    @Column(name = "coupon_id")
    private Integer couponId;

    /**
     * 患者
     */
    @Column(name = "patient_id")
    private Integer patientId;

    @Column(name = "coupon_name")
    private String couponName;

    /**
     * 患者姓名
     */
//    @Column(name = "patient_name")
//    private String patientName;

    @Column(name = "package_unit_price")
    private BigDecimal packageUnitPrice;
    private BigDecimal price;

    /**
     * 卡号
     */
    @Column(name = "card_number")
    private String cardNumber;

    /**
     * 售卖时间
     */
    @Column(name = "sold_date")
    private Date soldDate;

    /**
     * 是否有效
     */
    private Boolean inservice;

    /**
     * 创建人
     */
    @Column(name = "crt_id")
    private Integer crtId;

    /**
     * 创建时间
     */
    @Column(name = "crt_time")
    private Date crtTime;

    /**
     * 修改时间
     */
    @Column(name = "upd_time")
    private Date updTime;

    /**
     * 更新人
     */
    @Column(name = "upd_id")
    private Integer updId;
}