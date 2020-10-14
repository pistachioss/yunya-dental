package com.yunya.models.middletable;

import javax.persistence.Column;
import javax.persistence.Table;
import java.math.BigDecimal;

@Table(name = "base_coupon_item")
public class BaseCouponItem {
    /**
     * 产品ID
     */
    @Column(name = "coupon_id")
    private Integer couponId;

    /**
     * 明细类型(0-价目、1-商品)
     */
    private Integer type;

    /**
     * 明细选择范围(0全部、１分类、２项目)
     */
    @Column(name = "choice_rang_type")
    private Byte choiceRangType;

    /**
     * 对应明细ID(分类、项目)
     */
    @Column(name = "item_id")
    private Integer itemId;

    /**
     * 售出单价
     */
    @Column(name = "package_unit_price")
    private BigDecimal packageUnitPrice;

    /**
     * 数量
     */
    private Integer quantity;

    /**
     * 单个工作量
     */
    @Column(name = "workload_load")
    private BigDecimal workloadLoad;

    /**
     * 获取产品ID
     *
     * @return coupon_id - 产品ID
     */
    public Integer getCouponId() {
        return couponId;
    }

    /**
     * 设置产品ID
     *
     * @param couponId 产品ID
     */
    public void setCouponId(Integer couponId) {
        this.couponId = couponId;
    }

    /**
     * 获取明细类型(0-价目、1-商品)
     *
     * @return type - 明细类型(0-价目、1-商品)
     */
    public Integer getType() {
        return type;
    }

    /**
     * 设置明细类型(0-价目、1-商品)
     *
     * @param type 明细类型(0-价目、1-商品)
     */
    public void setType(Integer type) {
        this.type = type;
    }

    /**
     * 获取明细选择范围(0全部、１分类、２项目)
     *
     * @return choice_rang_type - 明细选择范围(0全部、１分类、２项目)
     */
    public Byte getChoiceRangType() {
        return choiceRangType;
    }

    /**
     * 设置明细选择范围(0全部、１分类、２项目)
     *
     * @param choiceRangType 明细选择范围(0全部、１分类、２项目)
     */
    public void setChoiceRangType(Byte choiceRangType) {
        this.choiceRangType = choiceRangType;
    }

    /**
     * 获取对应明细ID(分类、项目)
     *
     * @return item_id - 对应明细ID(分类、项目)
     */
    public Integer getItemId() {
        return itemId;
    }

    /**
     * 设置对应明细ID(分类、项目)
     *
     * @param itemId 对应明细ID(分类、项目)
     */
    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    /**
     * 获取售出单价
     *
     * @return package_unit_price - 售出单价
     */
    public BigDecimal getPackageUnitPrice() {
        return packageUnitPrice;
    }

    /**
     * 设置售出单价
     *
     * @param packageUnitPrice 售出单价
     */
    public void setPackageUnitPrice(BigDecimal packageUnitPrice) {
        this.packageUnitPrice = packageUnitPrice;
    }

    /**
     * 获取数量
     *
     * @return quantity - 数量
     */
    public Integer getQuantity() {
        return quantity;
    }

    /**
     * 设置数量
     *
     * @param quantity 数量
     */
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    /**
     * 获取单个工作量
     *
     * @return workload_load - 单个工作量
     */
    public BigDecimal getWorkloadLoad() {
        return workloadLoad;
    }

    /**
     * 设置单个工作量
     *
     * @param workloadLoad 单个工作量
     */
    public void setWorkloadLoad(BigDecimal workloadLoad) {
        this.workloadLoad = workloadLoad;
    }
}