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
 * 订单优惠
 */
@Getter
@Setter
@Table(name = "order_benefit")
public class OrderBenefit implements Serializable {
    @Id
    @GeneratedValue(generator = "JDBC", strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 订单id
     */
    @Column(name = "org_id")
    private Integer orderId;

    /**
     * 优惠总金额
     */
    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    /**
     * 授权人
     */
    @Column(name = "authorized_id")
    private Integer authorizedId;

    /**
     * 优惠方式（0-卡券优惠  1-授权折扣）
     */
    @Column(name = "benefit_type")
    private Integer benefitType;

    /**
     * 备注
     */
    private String remark;

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