package com.yunya.models.middletable;

import javax.persistence.Column;
import javax.persistence.Table;
import java.math.BigDecimal;

@Table(name = "base_tariff")
public class BaseTariff {
    /**
     * 项目ID
     */
    @Column(name = "item_id")
    private Integer itemId;

    /**
     * 项目类型（0-价目表；1-商品）
     */
    @Column(name = "item_type")
    private Byte itemType;

    /**
     * 父项目分类
     */
    @Column(name = "category_id")
    private Integer categoryId;

    /**
     * 项目分类名称
     */
    @Column(name = "category_name")
    private String categoryName;

    /**
     * 项目编码
     */
    @Column(name = "item_num")
    private String itemNum;

    /**
     * 项目名称
     */
    @Column(name = "item_name")
    private String itemName;

    /**
     * 单位
     */
    private String unit;

    /**
     * 基础价格
     */
    private BigDecimal price;

    /**
     * 获取项目ID
     *
     * @return item_id - 项目ID
     */
    public Integer getItemId() {
        return itemId;
    }

    /**
     * 设置项目ID
     *
     * @param itemId 项目ID
     */
    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    /**
     * 获取项目类型（0-价目表；1-商品）
     *
     * @return item_type - 项目类型（0-价目表；1-商品）
     */
    public Byte getItemType() {
        return itemType;
    }

    /**
     * 设置项目类型（0-价目表；1-商品）
     *
     * @param itemType 项目类型（0-价目表；1-商品）
     */
    public void setItemType(Byte itemType) {
        this.itemType = itemType;
    }

    /**
     * 获取父项目分类
     *
     * @return category_id - 父项目分类
     */
    public Integer getCategoryId() {
        return categoryId;
    }

    /**
     * 设置父项目分类
     *
     * @param categoryId 父项目分类
     */
    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    /**
     * 获取项目分类名称
     *
     * @return category_name - 项目分类名称
     */
    public String getCategoryName() {
        return categoryName;
    }

    /**
     * 设置项目分类名称
     *
     * @param categoryName 项目分类名称
     */
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    /**
     * 获取项目编码
     *
     * @return item_num - 项目编码
     */
    public String getItemNum() {
        return itemNum;
    }

    /**
     * 设置项目编码
     *
     * @param itemNum 项目编码
     */
    public void setItemNum(String itemNum) {
        this.itemNum = itemNum;
    }

    /**
     * 获取项目名称
     *
     * @return item_name - 项目名称
     */
    public String getItemName() {
        return itemName;
    }

    /**
     * 设置项目名称
     *
     * @param itemName 项目名称
     */
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    /**
     * 获取单位
     *
     * @return unit - 单位
     */
    public String getUnit() {
        return unit;
    }

    /**
     * 设置单位
     *
     * @param unit 单位
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }

    /**
     * 获取基础价格
     *
     * @return price - 基础价格
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * 设置基础价格
     *
     * @param price 基础价格
     */
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}