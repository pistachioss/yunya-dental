package com.yunya.models.discount;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Table(name = "deduction_item_period")
public class DeductionItemPeriod {
    /**
     * 主键
     */
    @Id
    private Integer id;

    /**
     * 卡券ID
     */
    @Column(name = "coupon_id")
    private Integer couponId;

    /**
     * 类型 0:基础价目表,1:基础商品表 2:选项目明细
     */
    private Integer type;

    /**
     * 明细ID
     */
    @Column(name = "item_id")
    private Integer itemId;

    /**
     * 数量
     */
    private Integer count;

    /**
     * 套餐单价
     */
    @Column(name = "package_unit_price")
    private BigDecimal packageUnitPrice;

    /**
     * 套餐价
     */
    @Column(name = "sale_amount")
    private BigDecimal saleAmount;

    /**
     * 单个明细工作量
     */
    @Column(name = "workload_load")
    private BigDecimal workloadLoad;

    @Column(name = "unit_price")
    private BigDecimal unitPrice;

    private BigDecimal price;

    /**
     * 套餐券匹配项目小程序是否显示 0否 1是
     */
    @Column(name = "is_show_app")
    private Integer isShowApp;
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

}