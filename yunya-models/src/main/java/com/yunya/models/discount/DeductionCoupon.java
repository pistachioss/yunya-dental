package com.yunya.models.discount;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "deduction_coupon")
@Data
public class DeductionCoupon {
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
     * 是否可混合使用优惠 0.不可以共用 1.可以共用
     */
    private Integer mixable;

    /**
     * 可使用门诊
     */
    @Column(name = "useable_clinic")
    private String useableClinic;

    /**
     * 使用方式 0:一次使用 1:多次使用
     */
    @Column(name = "use_way")
    private Byte useWay;

    /**
     * 账单单次使用限制数量
     */
    @Column(name = "limit_count")
    private Integer limitCount;

    /**
     * 是否可与他人共享
     */
    @Column(name = "is_share")
    private Boolean isShare;

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