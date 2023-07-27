package com.yunya.models.discount;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;
@Data
@Table(name = "coupon_refund")
public class CouponRefund {
    /**
     * 退费记录ID
     */
    @Id
    private Integer id;

    /**
     * 组织ID
     */
    @Column(name = "org_id")
    private Integer orgId;

    /**
     * 患者ID
     */
    @Column(name = "patient_id")
    private Integer patientId;

    /**
     * 开单记录ID
     */
    @Column(name = "order_id")
    private Integer orderId;

    /**
     * 退款总额
     */
    @Column(name = "refund_amount")
    private BigDecimal refundAmount;

    /**
     * 退费原因
     */
    private String reason;

    /**
     * 退费凭证(多个用法逗号隔开)
     */
    @Column(name = "refund_certificate")
    private String refundCertificate;

    /**
     * 备注
     */
    private String remark;

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

    /**
     * 更新人ID
     */
    @Column(name = "upd_id")
    private Integer updId;

    /**
     * 更新时间
     */
    @Column(name = "upd_time")
    private Date updTime;

}