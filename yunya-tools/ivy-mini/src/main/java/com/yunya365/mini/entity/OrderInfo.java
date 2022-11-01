package com.yunya365.mini.entity;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Table(name = "order_info")
@Data
public class OrderInfo {
    @Id
    @GeneratedValue(generator = "JDBC", strategy = GenerationType.IDENTITY)
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
     * 父订单id
     */
    @Column(name = "parent_order_id")
    private Integer parentOrderId;

    /**
     * 微信产生的订单号
     */
    @Column(name = "out_order_no")
    private String outOrderNo;

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
     * 订单状态：0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->申请退款；6-退款成功
     */
    private Byte status;

    /**
     * 是否核销(0-否 1-是)
     */
    @Column(name = "active_status")
    private Boolean activeStatus;

    /**
     * 订单类型：0->正常订单；1->秒杀订单；2-拼团订单
     */
    @Column(name = "order_type")
    private Byte orderType;

    /**
     * 商品类型：0-商品 1-虚拟服务
     */
    @Column(name = "product_type")
    private Byte productType;

    /**
     * 配送方式：0->自提 1->配送
     */
    @Column(name = "delivery_type")
    private Byte deliveryType;

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
     * 自提门诊
     */
    @Column(name = "pick_up_clinic")
    private String pickUpClinic;

    /**
     * 收货人姓名
     */
    @Column(name = "receiver_name")
    private String receiverName;

    /**
     * 收货人电话
     */
    @Column(name = "receiver_phone")
    private String receiverPhone;

    /**
     * 收货人邮编
     */
    @Column(name = "receiver_post_code")
    private String receiverPostCode;

    /**
     * 省份/直辖市
     */
    @Column(name = "receiver_province")
    private String receiverProvince;

    /**
     * 城市
     */
    @Column(name = "receiver_city")
    private String receiverCity;

    /**
     * 区
     */
    @Column(name = "receiver_region")
    private String receiverRegion;

    /**
     * 详细地址
     */
    @Column(name = "receiver_detail_address")
    private String receiverDetailAddress;

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
     * 是否有子单（0-无 1-有）
     */
    @Column(name = "has_sub")
    private Boolean hasSub;

    /**
     * 更新时间
     */
    @Column(name = "upd_time")
    private Date updTime;

    /**
     * 创建时间
     */
    @Column(name = "crt_time")
    private Date crtTime;

    @Column(name = "upd_id")
    private Integer updId;

}