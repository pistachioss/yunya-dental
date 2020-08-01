package com.yunya.models.discount;

import java.util.Date;
import javax.persistence.*;

@Table(name = "coupon_common_info")
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
     * 卡券类型（0-代金券；1-折扣券；2-套餐券；3-充值卡）
     */
    private Byte type;

    /**
     * 图像地址
     */
    private String path;

    /**
     * 卡券名称
     */
    private String name;

    /**
     * 是否制作实体卡（0-否；1-是）
     */
    @Column(name = "physical_card")
    private Boolean physicalCard;

    /**
     * 是否启用
     */
    private Boolean inservice;

    /**
     * 创建人ID
     */
    @Column(name = "crt_id")
    private Integer crtId;

    /**
     * 创建人姓名
     */
    @Column(name = "crt_name")
    private String crtName;

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
     * 更新人姓名
     */
    @Column(name = "upd_name")
    private String updName;

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
     * 获取产品分类ID
     *
     * @return product_type_id - 产品分类ID
     */
    public Integer getProductTypeId() {
        return productTypeId;
    }

    /**
     * 设置产品分类ID
     *
     * @param productTypeId 产品分类ID
     */
    public void setProductTypeId(Integer productTypeId) {
        this.productTypeId = productTypeId;
    }

    /**
     * 获取卡券类型（0-代金券；1-折扣券；2-套餐券；3-充值卡）
     *
     * @return type - 卡券类型（0-代金券；1-折扣券；2-套餐券；3-充值卡）
     */
    public Byte getType() {
        return type;
    }

    /**
     * 设置卡券类型（0-代金券；1-折扣券；2-套餐券；3-充值卡）
     *
     * @param type 卡券类型（0-代金券；1-折扣券；2-套餐券；3-充值卡）
     */
    public void setType(Byte type) {
        this.type = type;
    }

    /**
     * 获取图片地址
     *
     * @return path
     */
    public String getPath() {
        return path;
    }

    /**
     * 设置图像地址
     *
     * @param path 图像地址
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * 获取卡券名称
     *
     * @return name - 卡券名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置卡券名称
     *
     * @param name 卡券名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取是否制作实体卡（0-否；1-是）
     *
     * @return physical_card - 是否制作实体卡（0-否；1-是）
     */
    public Boolean getPhysicalCard() {
        return physicalCard;
    }

    /**
     * 设置是否制作实体卡（0-否；1-是）
     *
     * @param physicalCard 是否制作实体卡（0-否；1-是）
     */
    public void setPhysicalCard(Boolean physicalCard) {
        this.physicalCard = physicalCard;
    }

    /**
     * 获取是否启用
     *
     * @return inservice - 是否启用
     */
    public Boolean getInservice() {
        return inservice;
    }

    /**
     * 设置是否启用
     *
     * @param inservice 是否启用
     */
    public void setInservice(Boolean inservice) {
        this.inservice = inservice;
    }

    /**
     * 获取创建人ID
     *
     * @return crt_id - 创建人ID
     */
    public Integer getCrtId() {
        return crtId;
    }

    /**
     * 设置创建人ID
     *
     * @param crtId 创建人ID
     */
    public void setCrtId(Integer crtId) {
        this.crtId = crtId;
    }

    /**
     * 获取创建人姓名
     *
     * @return crt_name - 创建人姓名
     */
    public String getCrtName() {
        return crtName;
    }

    /**
     * 设置创建人姓名
     *
     * @param crtName 创建人姓名
     */
    public void setCrtName(String crtName) {
        this.crtName = crtName;
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
     * 获取更新人ID
     *
     * @return upd_id - 更新人ID
     */
    public Integer getUpdId() {
        return updId;
    }

    /**
     * 设置更新人ID
     *
     * @param updId 更新人ID
     */
    public void setUpdId(Integer updId) {
        this.updId = updId;
    }

    /**
     * 获取更新人姓名
     *
     * @return upd_name - 更新人姓名
     */
    public String getUpdName() {
        return updName;
    }

    /**
     * 设置更新人姓名
     *
     * @param updName 更新人姓名
     */
    public void setUpdName(String updName) {
        this.updName = updName;
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