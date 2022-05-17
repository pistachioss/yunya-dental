package com.yunya365.mini.entity;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;

@Table(name = "order_item")
public class OrderItem {
    @Id
    private Integer id;

    /**
     * 订单id
     */
    @Column(name = "order_id")
    private Integer orderId;

    /**
     * 订单号
     */
    @Column(name = "order_sn")
    private Integer orderSn;

    /**
     * 商品id
     */
    @Column(name = "product_id")
    private Integer productId;

    /**
     * 商品号码
     */
    @Column(name = "product_sn")
    private String productSn;

    /**
     * 商品价格
     */
    @Column(name = "product_price")
    private BigDecimal productPrice;

    /**
     * 商品名称
     */
    @Column(name = "product_name")
    private String productName;

    /**
     * 购买数量
     */
    @Column(name = "product_quantity")
    private Integer productQuantity;

    /**
     * 商品分类id
     */
    @Column(name = "product_category_id")
    private Integer productCategoryId;

    /**
     * 商品分类名称
     */
    @Column(name = "product_category_name")
    private String productCategoryName;

    /**
     * 创建时间
     */
    @Column(name = "crt_time")
    private Date crtTime;

    /**
     * 更新时间
     */
    @Column(name = "upd_time")
    private Date updTime;

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取订单id
     *
     * @return order_id - 订单id
     */
    public Integer getOrderId() {
        return orderId;
    }

    /**
     * 设置订单id
     *
     * @param orderId 订单id
     */
    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    /**
     * 获取订单号
     *
     * @return order_sn - 订单号
     */
    public Integer getOrderSn() {
        return orderSn;
    }

    /**
     * 设置订单号
     *
     * @param orderSn 订单号
     */
    public void setOrderSn(Integer orderSn) {
        this.orderSn = orderSn;
    }

    /**
     * 获取商品id
     *
     * @return product_id - 商品id
     */
    public Integer getProductId() {
        return productId;
    }

    /**
     * 设置商品id
     *
     * @param productId 商品id
     */
    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    /**
     * 获取商品号码
     *
     * @return product_sn - 商品号码
     */
    public String getProductSn() {
        return productSn;
    }

    /**
     * 设置商品号码
     *
     * @param productSn 商品号码
     */
    public void setProductSn(String productSn) {
        this.productSn = productSn;
    }

    /**
     * 获取商品价格
     *
     * @return product_price - 商品价格
     */
    public BigDecimal getProductPrice() {
        return productPrice;
    }

    /**
     * 设置商品价格
     *
     * @param productPrice 商品价格
     */
    public void setProductPrice(BigDecimal productPrice) {
        this.productPrice = productPrice;
    }

    /**
     * 获取商品名称
     *
     * @return product_name - 商品名称
     */
    public String getProductName() {
        return productName;
    }

    /**
     * 设置商品名称
     *
     * @param productName 商品名称
     */
    public void setProductName(String productName) {
        this.productName = productName;
    }

    /**
     * 获取购买数量
     *
     * @return product_quantity - 购买数量
     */
    public Integer getProductQuantity() {
        return productQuantity;
    }

    /**
     * 设置购买数量
     *
     * @param productQuantity 购买数量
     */
    public void setProductQuantity(Integer productQuantity) {
        this.productQuantity = productQuantity;
    }

    /**
     * 获取商品分类id
     *
     * @return product_category_id - 商品分类id
     */
    public Integer getProductCategoryId() {
        return productCategoryId;
    }

    /**
     * 设置商品分类id
     *
     * @param productCategoryId 商品分类id
     */
    public void setProductCategoryId(Integer productCategoryId) {
        this.productCategoryId = productCategoryId;
    }

    /**
     * 获取商品分类名称
     *
     * @return product_category_name - 商品分类名称
     */
    public String getProductCategoryName() {
        return productCategoryName;
    }

    /**
     * 设置商品分类名称
     *
     * @param productCategoryName 商品分类名称
     */
    public void setProductCategoryName(String productCategoryName) {
        this.productCategoryName = productCategoryName;
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