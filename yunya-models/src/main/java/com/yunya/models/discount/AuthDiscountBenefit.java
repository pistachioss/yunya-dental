package com.yunya.models.discount;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author 
 * 授权折扣优惠
 */
@Getter
@Setter
@Table(name = "auth_discount_benefit")
public class AuthDiscountBenefit implements Serializable {
    @Id
    @GeneratedValue(generator = "JDBC", strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 组织id
     */
    @Column(name = "org_id")
    private Integer orgId;

    /**
     * 订单id
     */
    @Column(name = "order_id")
    private Integer orderId;

    /**
     * 订单明细id
     */
    @Column(name = "order_detail_id")
    private Integer orderDetailId;

    /**
     * 患者id（优惠券使用人）
     */
    @Column(name = "patient_id")
    private Integer patientId;

    /**
     * 项目id
     */
    @Column(name = "item_id")
    private Integer itemId;

    /**
     * 项目类型
     */
    @Column(name = "item_type")
    private Integer itemType;

    /**
     * 优惠金额
     */
    @Column(name = "benefit_amount")
    private BigDecimal benefitAmount;

    /**
     * 操作方式
     */
    @Column(name = "operate_type")
    private Integer operateType;

    /**
     * 是否删除（0:否  1:是）
     */
    @Column(name = "is_deleted")
    private Integer deleted;

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