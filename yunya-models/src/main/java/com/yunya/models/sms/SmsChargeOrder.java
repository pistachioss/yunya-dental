package com.yunya.models.sms;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Table(name = "sms_charge_order")
public class SmsChargeOrder {
    @Id
    private Integer id;

    /**
     * 组织id（门诊或公司）
     */
    @Column(name = "org_id")
    private Integer orgId;

    /**
     * 订单号
     */
    @Column(name = "order_no")
    private String orderNo;

    /**
     * 短信条数
     */
    @Column(name = "sms_num")
    private Integer smsNum;

    /**
     * 短信价格
     */
    private BigDecimal price;

    /**
     * 采商订单号
     */
    @Column(name = "cb_order_no")
    private String cbOrderNo;

    /**
     * 支付宝或微信的交易订单号
     */
    @Column(name = "out_order_no")
    private String outOrderNo;

    /**
     * 支付结果
     */
    @Column(name = "out_result")
    private String outResult;

    /**
     * 付款方式
     */
    @Column(name = "payment_channel")
    private String paymentChannel;

    /**
     * 商品名称
     */
    @Column(name = "sms_goods")
    private String smsGoods;

    /**
     * 订单状态：0-等待付款，1-付款成功，2-付款失败，3-已关闭
     */
    @Column(name = "order_status")
    private Byte orderStatus;

    /**
     * 充值时间
     */
    @Column(name = "pay_time")
    private Date payTime;

    /**
     * 充值人id
     */
    @Column(name = "crt_id")
    private Integer crtId;

    /**
     * 充值人
     */
    @Column(name = "crt_user")
    private String crtUser;

    /**
     * 创建时间
     */
    @Column(name = "crt_time")
    private Date crtTime;

    /**
     * 修改人id
     */
    @Column(name = "upt_id")
    private Integer uptId;

    /**
     * 修改时间
     */
    @Column(name = "upt_time")
    private Date uptTime;

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
     * 获取组织id（门诊或公司）
     *
     * @return org_id - 组织id（门诊或公司）
     */
    public Integer getOrgId() {
        return orgId;
    }

    /**
     * 设置组织id（门诊或公司）
     *
     * @param orgId 组织id（门诊或公司）
     */
    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    /**
     * 获取短信条数
     *
     * @return sms_num - 短信条数
     */
    public Integer getSmsNum() {
        return smsNum;
    }

    /**
     * 设置短信条数
     *
     * @param smsNum 短信条数
     */
    public void setSmsNum(Integer smsNum) {
        this.smsNum = smsNum;
    }

    /**
     * 获取订单号
     *
     * @return
     */
    public String getOrderNo() {
        return orderNo;
    }

    /**
     * 设置订单号
     * @param orderNo
     */
    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    /**
     * 获取短信价格
     *
     * @return price - 短信价格
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * 设置短信价格
     *
     * @param price 短信价格
     */
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    /**
     * 获取采商订单号
     *
     * @return cb_order_no - 采商订单号
     */
    public String getCbOrderNo() {
        return cbOrderNo;
    }

    /**
     * 设置采商订单号
     *
     * @param cbOrderNo 采商订单号
     */
    public void setCbOrderNo(String cbOrderNo) {
        this.cbOrderNo = cbOrderNo;
    }

    /**
     * 获取支付宝或微信的交易订单号
     *
     * @return 支付宝或微信的交易订单号
     */
    public String getOutOrderNo() {
        return outOrderNo;
    }

    /**
     * 设置支付宝或微信的交易订单号
     *
     * @param outOrderNo 支付宝或微信的交易订单号
     */
    public void setOutOrderNo(String outOrderNo) {
        this.outOrderNo = outOrderNo;
    }

    /**
     * 获取支付结果
     *
     * @return out_result - 支付结果
     */
    public String getOutResult() {
        return outResult;
    }

    /**
     * 设置支付结果
     *
     * @param outResult 支付结果
     */
    public void setOutResult(String outResult) {
        this.outResult = outResult;
    }

    /**
     * 获取付款方式
     *
     * @return payment_channel - 付款方式
     */
    public String getPaymentChannel() {
        return paymentChannel;
    }

    /**
     * 设置付款方式
     *
     * @param paymentChannel 付款方式
     */
    public void setPaymentChannel(String paymentChannel) {
        this.paymentChannel = paymentChannel;
    }

    /**
     * 获取商品名称
     *
     * @return sms_goods - 商品名称
     */
    public String getSmsGoods() {
        return smsGoods;
    }

    /**
     * 设置商品名称
     *
     * @param smsGoods 商品名称
     */
    public void setSmsGoods(String smsGoods) {
        this.smsGoods = smsGoods;
    }

    /**
     * 获取订单状态：0-等待付款，1-付款成功，2-付款失败，3-已关闭
     *
     * @return order_status - 订单状态：0-等待付款，1-付款成功，2-付款失败，3-已关闭
     */
    public Byte getOrderStatus() {
        return orderStatus;
    }

    /**
     * 设置订单状态：0-等待付款，1-付款成功，2-付款失败，3-已关闭
     *
     * @param orderStatus 订单状态：0-等待付款，1-付款成功，2-付款失败，3-已关闭
     */
    public void setOrderStatus(Byte orderStatus) {
        this.orderStatus = orderStatus;
    }

    /**
     * 获取充值时间
     *
     * @return
     */
    public Date getPayTime() {
        return payTime;
    }

    /**
     * 设置充值时间
     *
     * @param payTime
     */
    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    /**
     * 获取充值人id
     *
     * @return crt_id - 充值人id
     */
    public Integer getCrtId() {
        return crtId;
    }

    /**
     * 设置充值人id
     *
     * @param crtId 充值人id
     */
    public void setCrtId(Integer crtId) {
        this.crtId = crtId;
    }

    /**
     * 获取充值人
     *
     * @return crt_user - 充值人
     */
    public String getCrtUser() {
        return crtUser;
    }

    /**
     * 设置充值人
     *
     * @param crtUser 充值人
     */
    public void setCrtUser(String crtUser) {
        this.crtUser = crtUser;
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

    /**
     * 获取修改人id
     *
     * @return upt_id - 修改人id
     */
    public Integer getUptId() {
        return uptId;
    }

    /**
     * 设置修改人id
     *
     * @param uptId 修改人id
     */
    public void setUptId(Integer uptId) {
        this.uptId = uptId;
    }

    /**
     * 获取修改时间
     *
     * @return upt_time - 修改时间
     */
    public Date getUptTime() {
        return uptTime;
    }

    /**
     * 设置修改时间
     *
     * @param uptTime 修改时间
     */
    public void setUptTime(Date uptTime) {
        this.uptTime = uptTime;
    }
}