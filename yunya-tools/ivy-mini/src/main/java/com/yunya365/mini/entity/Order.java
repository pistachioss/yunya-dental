package com.yunya365.mini.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class Order {
    @Id
    private Integer id;

    /**
     * 微信用户id
     */
    @Column(name = "fans_id")
    private Integer fansId;

    /**
     * 订单编号
     */
    @Column(name = "order_sn")
    private String orderSn;

    /**
     * 订单总金额
     */
    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    /**
     * 应付金额（实际支付金额）
     */
    @Column(name = "pay_amount")
    private BigDecimal payAmount;

    /**
     * 运费金额
     */
    @Column(name = "freight_amount")
    private BigDecimal freightAmount;

    /**
     * 优惠券抵扣金额
     */
    @Column(name = "coupon_amount")
    private BigDecimal couponAmount;

    /**
     * 支付方式：0->未支付；1->支付宝；2->微信
     */
    @Column(name = "pay_type")
    private Byte payType;

    /**
     * 订单来源：0->PC订单；1->小程序订单
     */
    @Column(name = "source_type")
    private Byte sourceType;

    /**
     * 订单状态：0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->无效订单
     */
    private Byte status;

    /**
     * 订单类型：0->正常订单；1->秒杀订单；2-拼团订单
     */
    @Column(name = "order_type")
    private Byte orderType;

    /**
     * 物流公司(配送方式)
     */
    @Column(name = "delivery_company")
    private String deliveryCompany;

    /**
     * 物流单号
     */
    @Column(name = "delivery_sn")
    private String deliverySn;

    /**
     * 订单备注
     */
    private String remark;

    /**
     * 确认收货状态：0->未确认；1->已确认
     */
    @Column(name = "confirm_status")
    private Byte confirmStatus;

    /**
     * 删除状态：0->未删除；1->已删除
     */
    @Column(name = "delete_status")
    private Byte deleteStatus;

    /**
     * 支付时间
     */
    @Column(name = "payment_time")
    private Date paymentTime;

    /**
     * 发货时间
     */
    @Column(name = "delivery_time")
    private Date deliveryTime;

    /**
     * 确认收货时间
     */
    @Column(name = "receive_time")
    private Date receiveTime;

    /**
     * 评价时间
     */
    @Column(name = "comment_time")
    private Date commentTime;

    /**
     * 修改时间
     */
    @Column(name = "modify_time")
    private Date modifyTime;

    /**
     * 创建时间
     */
    @Column(name = "crt_time")
    private Date crtTime;
}