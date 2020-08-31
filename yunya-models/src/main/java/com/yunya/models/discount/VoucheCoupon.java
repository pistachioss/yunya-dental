package com.yunya.models.discount;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;

@Table(name = "vouche_coupon")
@Data
public class VoucheCoupon {
    /**
     * 主键
     */
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    /**
     * 卡券id
     */
    @Column(name = "coupon_id")
    private Integer couponId;

    /**
     * 面值
     */
    @Column(name = "face_value")
    private BigDecimal faceValue;

    /**
     * 产品有效期
     */
    @Column(name = "activation_deadline")
    private Date activationDeadline;

    /**
     * 卡券激活后有效期
     */
    @Column(name = "effective_days")
    private Integer effectiveDays;

    /**
     * 是否可混合使用优惠 0.可以混合使用1.跟会员卡混合使用2.无法混合使用
     */
    @Column(name = "mixed_use_type")
    private Byte mixedUseType;

    /**
     * 账单单次使用限制数量
     */
    @Column(name = "limit_count")
    private Integer limitCount;

    /**
     * 可使用门诊
     */
    @Column(name = "useable_clinci")
    private String useableClinci;

    /**
     * 是否可与他人共享
     */
    @Column(name = "is_share")
    private Boolean isShare;

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