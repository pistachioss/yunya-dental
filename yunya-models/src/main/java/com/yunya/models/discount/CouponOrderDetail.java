package com.yunya.models.discount;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;
@Data
@Table(name = "coupon_order_detail")
public class CouponOrderDetail {
    /**
     * 主键ID
     */
    @Id
    private Integer id;

    /**
     * 组织（门诊）ID
     */
    @Column(name = "org_id")
    private Integer orgId;

    /**
     * 开单记录ID
     */
    @Column(name = "order_record_id")
    private Integer orderRecordId;

    /**
     * 卡券类型（0-代金券；1-折扣券；2-兑换券；3-套餐券；4-充值券; 5-划扣券）
     */
    private Integer type;

    /**
     * 卡券id
     */
    @Column(name = "card_id")
    private Integer cardId;

    /**
     * 开单项目ID
     */
    @Column(name = "coupon_id")
    private Integer couponId;

    /**
     * 开单项目名称
     */
    @Column(name = "coupon_name")
    private String couponName;

    /**
     * 售出原价
     */
    private BigDecimal price;

    /**
     * 数量
     */
    private Integer quantity;

    /**
     * 应收金额
     */
    @Column(name = "receivable_amount")
    private BigDecimal receivableAmount;

    /**
     * 咨询师ID
     */
    @Column(name = "consulter_id")
    private Integer consulterId;

    /**
     * 执行人ID
     */
    @Column(name = "executor_id")
    private Integer executorId;

    @Column(name = "sale_channel_id")
    private Integer saleChannelId;

    /**
     * 备注
     */
    private String remarks;
    /**
     * 是否有效
     */
    private Boolean inservice;

    /**
     * 创建人ID
     */
    @Column(name = "crt_id")
    private Integer crtId;


    /**
     * 创建时间
     */
    @Column(name = "crt_time")
    private Date crtTime;
}