package com.yunya.models.discount;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Table(name = "coupon_common_info")
@Data
public class CouponCommonInfo {
    /**
     * 主键
     */
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    /**
     * 产品分类ID
     */
    @Column(name = "product_type_id")
    private Integer productTypeId;
    /**
     * 销售来源ID
     */
    @Column(name = "sales_source_id")
    private Integer salesSourceId;

    /**
     * 卡券类型（0-代金券；1-折扣券；2-兑换券；3-套餐券；4-充值券）
     */
    private Byte type;

    /**
     * 卡券名称
     */
    private String name;

    /**
     * 是否制作实体卡（0-否；1-是）
     */
    @Column(name = "is_make_physical_card")
    private Boolean isMakePhysicalCard;

    /**
     * 产品编码
     */
    @Column(name = "coupon_code")
    private String couponCode;

    /**
     * 售出金额
     */
    @Column(name = "sold_amount")
    private BigDecimal soldAmount;

    /**
     * 可售开始时间
     */
    @Column(name = "available_sale_start_date")
    private Date availableSaleStartDate;

    /**
     * 可售截止时间
     */
    @Column(name = "available_sale_end_date")
    private Date availableSaleEndDate;

    /**
     * 是否启用
     */
    @Column(name = "is_inservice")
    private Boolean isInservice;

    /**
     * 是否线上售卖(0:否 1:是)
     */
    @Column(name = "is_online_sale")
    private Boolean isOnlineSale;
    /**
     * 销量
     */
    private Integer sale;
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