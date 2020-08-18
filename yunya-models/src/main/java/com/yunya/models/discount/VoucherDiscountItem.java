package com.yunya.models.discount;

import java.util.Date;
import javax.persistence.*;

@Table(name = "voucher_discount_item")
public class VoucherDiscountItem {
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
     * 项目类型  0:基础价目表,1:基础商品表
     */
    private Integer type;

    /**
     * 选择范围  0:全选 1:选项目分类 2:选项目明细
     */
    @Column(name = "choice_rang_type")
    private Byte choiceRangType;

    /**
     * 明细ID
     */
    @Column(name = "item_id")
    private Integer itemId;

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
     * 获取项目类型  0:基础价目表,1:基础商品表
     *
     * @return type - 项目类型  0:基础价目表,1:基础商品表
     */
    public Integer getType() {
        return type;
    }

    /**
     * 设置项目类型  0:基础价目表,1:基础商品表
     *
     * @param type 项目类型  0:基础价目表,1:基础商品表
     */
    public void setType(Integer type) {
        this.type = type;
    }

    /**
     * 获取选择范围  0:全选 1:选项目分类 2:选项目明细
     *
     * @return choice_rang_type - 选择范围  0:全选 1:选项目分类 2:选项目明细
     */
    public Byte getChoiceRangType() {
        return choiceRangType;
    }

    /**
     * 设置选择范围  0:全选 1:选项目分类 2:选项目明细
     *
     * @param choiceRangType 选择范围  0:全选 1:选项目分类 2:选项目明细
     */
    public void setChoiceRangType(Byte choiceRangType) {
        this.choiceRangType = choiceRangType;
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
}