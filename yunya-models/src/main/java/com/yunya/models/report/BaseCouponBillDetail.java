package com.yunya.models.report;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

@Table(name = "base_coupon_bill_detail")
@Data
public class BaseCouponBillDetail {
    /**
     * 订单明细ID
     */
    @Id
    @Column(name = "order_detail_id")
    private Integer orderDetailId;

    /**
     * 组织ID
     */
    @Column(name = "org_id")
    private Integer orgId;

    /**
     * 订单ID
     */
    @Column(name = "order_id")
    private Integer orderId;

    /**
     * 礼包id
     */
    @Column(name = "coupon_id")
    private Integer couponId;

    /**
     * 卡券类型（0-代金券；1-折扣券；2-兑换券；3-套餐券；4-充值券; 5-划扣券）
     */
    private Integer type;

    /**
     * 礼包编码
     */
    @Column(name = "coupon_number")
    private String couponNumber;

    /**
     * 开单项目名称
     */
    @Column(name = "coupon_name")
    private String couponName;

    /**
     * 售出单价
     */
    private BigDecimal price;

    /**
     * 应收金额
     */
    @Column(name = "receivable_amount")
    private BigDecimal receivableAmount;

    /**
     * 数量
     */
    private Integer quantity;

    /**
     * 执行人ID
     */
    @Column(name = "executor_id")
    private Integer executorId;

    /**
     * 咨询师ID
     */
    @Column(name = "consulter_id")
    private Integer consulterId;


    /**
     * 销售渠道(艾维门诊)
     */
    @Column(name = "sale_channel_id")
    private Integer saleChannelId;

    /**
     * 项目收费金额
     */
    @Column(name = "received_amount")
    private BigDecimal receivedAmount;

    /**
     * 开单备注
     */
    private String remark;

}