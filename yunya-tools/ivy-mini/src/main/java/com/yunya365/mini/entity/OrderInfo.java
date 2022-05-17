package com.yunya365.mini.entity;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;

@Table(name = "order_info")
public class OrderInfo {
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

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取微信用户id
     *
     * @return fans_id - 微信用户id
     */
    public Integer getFansId() {
        return fansId;
    }

    /**
     * 设置微信用户id
     *
     * @param fansId 微信用户id
     */
    public void setFansId(Integer fansId) {
        this.fansId = fansId;
    }

    /**
     * 获取订单编号
     *
     * @return order_sn - 订单编号
     */
    public String getOrderSn() {
        return orderSn;
    }

    /**
     * 设置订单编号
     *
     * @param orderSn 订单编号
     */
    public void setOrderSn(String orderSn) {
        this.orderSn = orderSn;
    }

    /**
     * 获取订单总金额
     *
     * @return total_amount - 订单总金额
     */
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    /**
     * 设置订单总金额
     *
     * @param totalAmount 订单总金额
     */
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    /**
     * 获取应付金额（实际支付金额）
     *
     * @return pay_amount - 应付金额（实际支付金额）
     */
    public BigDecimal getPayAmount() {
        return payAmount;
    }

    /**
     * 设置应付金额（实际支付金额）
     *
     * @param payAmount 应付金额（实际支付金额）
     */
    public void setPayAmount(BigDecimal payAmount) {
        this.payAmount = payAmount;
    }

    /**
     * 获取运费金额
     *
     * @return freight_amount - 运费金额
     */
    public BigDecimal getFreightAmount() {
        return freightAmount;
    }

    /**
     * 设置运费金额
     *
     * @param freightAmount 运费金额
     */
    public void setFreightAmount(BigDecimal freightAmount) {
        this.freightAmount = freightAmount;
    }

    /**
     * 获取优惠券抵扣金额
     *
     * @return coupon_amount - 优惠券抵扣金额
     */
    public BigDecimal getCouponAmount() {
        return couponAmount;
    }

    /**
     * 设置优惠券抵扣金额
     *
     * @param couponAmount 优惠券抵扣金额
     */
    public void setCouponAmount(BigDecimal couponAmount) {
        this.couponAmount = couponAmount;
    }

    /**
     * 获取支付方式：0->未支付；1->支付宝；2->微信
     *
     * @return pay_type - 支付方式：0->未支付；1->支付宝；2->微信
     */
    public Byte getPayType() {
        return payType;
    }

    /**
     * 设置支付方式：0->未支付；1->支付宝；2->微信
     *
     * @param payType 支付方式：0->未支付；1->支付宝；2->微信
     */
    public void setPayType(Byte payType) {
        this.payType = payType;
    }

    /**
     * 获取订单来源：0->PC订单；1->小程序订单
     *
     * @return source_type - 订单来源：0->PC订单；1->小程序订单
     */
    public Byte getSourceType() {
        return sourceType;
    }

    /**
     * 设置订单来源：0->PC订单；1->小程序订单
     *
     * @param sourceType 订单来源：0->PC订单；1->小程序订单
     */
    public void setSourceType(Byte sourceType) {
        this.sourceType = sourceType;
    }

    /**
     * 获取订单状态：0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->无效订单
     *
     * @return status - 订单状态：0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->无效订单
     */
    public Byte getStatus() {
        return status;
    }

    /**
     * 设置订单状态：0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->无效订单
     *
     * @param status 订单状态：0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->无效订单
     */
    public void setStatus(Byte status) {
        this.status = status;
    }

    /**
     * 获取订单类型：0->正常订单；1->秒杀订单；2-拼团订单
     *
     * @return order_type - 订单类型：0->正常订单；1->秒杀订单；2-拼团订单
     */
    public Byte getOrderType() {
        return orderType;
    }

    /**
     * 设置订单类型：0->正常订单；1->秒杀订单；2-拼团订单
     *
     * @param orderType 订单类型：0->正常订单；1->秒杀订单；2-拼团订单
     */
    public void setOrderType(Byte orderType) {
        this.orderType = orderType;
    }

    /**
     * 获取物流公司(配送方式)
     *
     * @return delivery_company - 物流公司(配送方式)
     */
    public String getDeliveryCompany() {
        return deliveryCompany;
    }

    /**
     * 设置物流公司(配送方式)
     *
     * @param deliveryCompany 物流公司(配送方式)
     */
    public void setDeliveryCompany(String deliveryCompany) {
        this.deliveryCompany = deliveryCompany;
    }

    /**
     * 获取物流单号
     *
     * @return delivery_sn - 物流单号
     */
    public String getDeliverySn() {
        return deliverySn;
    }

    /**
     * 设置物流单号
     *
     * @param deliverySn 物流单号
     */
    public void setDeliverySn(String deliverySn) {
        this.deliverySn = deliverySn;
    }

    /**
     * 获取订单备注
     *
     * @return remark - 订单备注
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置订单备注
     *
     * @param remark 订单备注
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * 获取确认收货状态：0->未确认；1->已确认
     *
     * @return confirm_status - 确认收货状态：0->未确认；1->已确认
     */
    public Byte getConfirmStatus() {
        return confirmStatus;
    }

    /**
     * 设置确认收货状态：0->未确认；1->已确认
     *
     * @param confirmStatus 确认收货状态：0->未确认；1->已确认
     */
    public void setConfirmStatus(Byte confirmStatus) {
        this.confirmStatus = confirmStatus;
    }

    /**
     * 获取删除状态：0->未删除；1->已删除
     *
     * @return delete_status - 删除状态：0->未删除；1->已删除
     */
    public Byte getDeleteStatus() {
        return deleteStatus;
    }

    /**
     * 设置删除状态：0->未删除；1->已删除
     *
     * @param deleteStatus 删除状态：0->未删除；1->已删除
     */
    public void setDeleteStatus(Byte deleteStatus) {
        this.deleteStatus = deleteStatus;
    }

    /**
     * 获取支付时间
     *
     * @return payment_time - 支付时间
     */
    public Date getPaymentTime() {
        return paymentTime;
    }

    /**
     * 设置支付时间
     *
     * @param paymentTime 支付时间
     */
    public void setPaymentTime(Date paymentTime) {
        this.paymentTime = paymentTime;
    }

    /**
     * 获取发货时间
     *
     * @return delivery_time - 发货时间
     */
    public Date getDeliveryTime() {
        return deliveryTime;
    }

    /**
     * 设置发货时间
     *
     * @param deliveryTime 发货时间
     */
    public void setDeliveryTime(Date deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    /**
     * 获取确认收货时间
     *
     * @return receive_time - 确认收货时间
     */
    public Date getReceiveTime() {
        return receiveTime;
    }

    /**
     * 设置确认收货时间
     *
     * @param receiveTime 确认收货时间
     */
    public void setReceiveTime(Date receiveTime) {
        this.receiveTime = receiveTime;
    }

    /**
     * 获取评价时间
     *
     * @return comment_time - 评价时间
     */
    public Date getCommentTime() {
        return commentTime;
    }

    /**
     * 设置评价时间
     *
     * @param commentTime 评价时间
     */
    public void setCommentTime(Date commentTime) {
        this.commentTime = commentTime;
    }

    /**
     * 获取修改时间
     *
     * @return modify_time - 修改时间
     */
    public Date getModifyTime() {
        return modifyTime;
    }

    /**
     * 设置修改时间
     *
     * @param modifyTime 修改时间
     */
    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
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
}