package com.yunya.models.discount;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Table(name = "discount_coupon")
@Data
public class DiscountCoupon {
    /**
     * 主键
     */
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    /**
     * 优惠券id
     */
    @Column(name = "coupon_id")
    private Integer couponId;

    /**
     * 折扣率
     */
    @Column(name = "discount_rate")
    private BigDecimal discountRate;

    /**
     * 激活截至日期(产品有效期)
     */
    @Column(name = "activation_deadline")
    private Date activationDeadline;

    /**
     * 卡券激活后有效期
     */
    @Column(name = "effective_days")
    private Integer effectiveDays;

    /**
     * 是否可与他人共享
     */
    @Column(name = "is_share")
    private Boolean isShare;

    /**
     * 是否可混合使用优惠 0.不可以共用 1.可以共用
     */
    @Column(name = "mixable")
    private Integer mixable;


    /**
     * 可使用门诊
     */
    @Column(name = "useable_clinic")
    private String useableClinic;

    /**
     * 工作量比例
     */
    @Column(name = "workload_rate")
    private BigDecimal workloadRate;

    /**
     * 备注
     */
    private String remark;

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
     * 更新人
     */
    @Column(name = "upd_id")
    private Integer updId;

    /**
     * 更新时间
     */
    @Column(name = "upd_time")
    private Date updTime;

}