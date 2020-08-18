package com.yunya.models.discount;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;

@Table(name = "package_coupon_item")
public class PackageCouponItem {
    /**
     * 主键
     */
    @Id
    private Integer id;

    /**
     * 优惠券id
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
     * 售出单价
     */
    @Column(name = "sale_unit_price")
    private BigDecimal saleUnitPrice;

    /**
     * 售出金额
     */
    @Column(name = "sale_amount")
    private BigDecimal saleAmount;

    /**
     * 数量
     */
    private Integer count;

    /**
     * 单个明细工作量
     */
    @Column(name = "workload_rate")
    private Integer workloadRate;

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

    /**
     * 乐观锁
     */
    private Integer revision;

    /**
     * 获取主键
     *
     * @return id - 主键
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置主键
     *
     * @param id 主键
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取优惠券id
     *
     * @return coupon_id - 优惠券id
     */
    public Integer getCouponId() {
        return couponId;
    }

    /**
     * 设置优惠券id
     *
     * @param couponId 优惠券id
     */
    public void setCouponId(Integer couponId) {
        this.couponId = couponId;
    }

    /**
     * 获取类型 0:基础价目表,1:基础商品表 2:选项目明细
     *
     * @return type - 类型 0:基础价目表,1:基础商品表 2:选项目明细
     */
    public Integer getType() {
        return type;
    }

    /**
     * 设置类型 0:基础价目表,1:基础商品表 2:选项目明细
     *
     * @param type 类型 0:基础价目表,1:基础商品表 2:选项目明细
     */
    public void setType(Integer type) {
        this.type = type;
    }

    /**
     * 获取明细ID
     *
     * @return item_id - 明细ID
     */
    public Integer getItemId() {
        return itemId;
    }

    /**
     * 设置明细ID
     *
     * @param itemId 明细ID
     */
    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    /**
     * 获取售出单价
     *
     * @return sale_unit_price - 售出单价
     */
    public BigDecimal getSaleUnitPrice() {
        return saleUnitPrice;
    }

    /**
     * 设置售出单价
     *
     * @param saleUnitPrice 售出单价
     */
    public void setSaleUnitPrice(BigDecimal saleUnitPrice) {
        this.saleUnitPrice = saleUnitPrice;
    }

    /**
     * 获取售出金额
     *
     * @return sale_amount - 售出金额
     */
    public BigDecimal getSaleAmount() {
        return saleAmount;
    }

    /**
     * 设置售出金额
     *
     * @param saleAmount 售出金额
     */
    public void setSaleAmount(BigDecimal saleAmount) {
        this.saleAmount = saleAmount;
    }

    /**
     * 获取数量
     *
     * @return count - 数量
     */
    public Integer getCount() {
        return count;
    }

    /**
     * 设置数量
     *
     * @param count 数量
     */
    public void setCount(Integer count) {
        this.count = count;
    }

    /**
     * 获取单个明细工作量
     *
     * @return workload_rate - 单个明细工作量
     */
    public Integer getWorkloadRate() {
        return workloadRate;
    }

    /**
     * 设置单个明细工作量
     *
     * @param workloadRate 单个明细工作量
     */
    public void setWorkloadRate(Integer workloadRate) {
        this.workloadRate = workloadRate;
    }

    /**
     * 获取创建人
     *
     * @return crt_id - 创建人
     */
    public Integer getCrtId() {
        return crtId;
    }

    /**
     * 设置创建人
     *
     * @param crtId 创建人
     */
    public void setCrtId(Integer crtId) {
        this.crtId = crtId;
    }

    /**
     * 获取创建时间
     *
     * @return crt_time - 创建时间
     */
    public Date getCrtTime() {
        return crtTime;
    }

    /**
     * 设置创建时间
     *
     * @param crtTime 创建时间
     */
    public void setCrtTime(Date crtTime) {
        this.crtTime = crtTime;
    }

    /**
     * 获取更新人
     *
     * @return upd_id - 更新人
     */
    public Integer getUpdId() {
        return updId;
    }

    /**
     * 设置更新人
     *
     * @param updId 更新人
     */
    public void setUpdId(Integer updId) {
        this.updId = updId;
    }

    /**
     * 获取更新时间
     *
     * @return upd_time - 更新时间
     */
    public Date getUpdTime() {
        return updTime;
    }

    /**
     * 设置更新时间
     *
     * @param updTime 更新时间
     */
    public void setUpdTime(Date updTime) {
        this.updTime = updTime;
    }

    /**
     * 获取乐观锁
     *
     * @return revision - 乐观锁
     */
    public Integer getRevision() {
        return revision;
    }

    /**
     * 设置乐观锁
     *
     * @param revision 乐观锁
     */
    public void setRevision(Integer revision) {
        this.revision = revision;
    }
}