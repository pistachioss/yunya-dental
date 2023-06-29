package com.yunya.models.system;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Table(name = "member_type")
public class MemberType {
    /**
     * 主键ID
     */
    @Id
    private Integer id;

    /**
     * 会员卡名称
     */
    private String name;

    /**
     * 未激活时的会员卡名称
     */
    @Column(name = "old_name")
    private String oldName;

    /**
     * 主键ID
     */
    @Column(name = "next_level_id")
    private Integer nextLevelId;

    /**
     * 续费金额
     */
    @Column(name = "renewal_amount")
    private BigDecimal renewalAmount;

    /**
     * 充值达标获卡金额
     */
    @Column(name = "recharge_max_amount")
    private BigDecimal rechargeMaxAmount;

    /**
     * 累计消费达标获卡金额
     */
    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    /**
     * 差额补齐获卡金额
     */
    @Column(name = "recharge_sub_amount")
    private BigDecimal rechargeSubAmount;

    /**
     * 会员等级对应充值起充额
     */
    @Column(name = "recharge_min_amount")
    private BigDecimal rechargeMinAmount;

    /**
     * 年限
     */
    @Column(name = "age_limit")
    private Integer ageLimit;

    /**
     * 折扣率（价目表自动调价的折扣率）
     */
    private Float rate;

    /**
     * 类型,0:普通,1:VIP
     */
    private Byte type;

    /**
     * 图标
     */
    private String icon;

    @Column(name = "picture_code")
    private String pictureCode;

    /**
     * 会员卡描述（青藤、银藤、金藤、艾维会员）
     */
    private String description;

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
     * 创建人名称
     */
    @Column(name = "crt_name")
    private String crtName;

    /**
     * 创建时间
     */
    @Column(name = "crt_time")
    private Date crtTime;

    @Column(name = "upd_id")
    private Integer updId;

    /**
     * 修改人名称
     */
    @Column(name = "upd_name")
    private String updName;

    /**
     * 修改时间
     */
    @Column(name = "upd_time")
    private Date updTime;

    public String getOldName() {
        return oldName;
    }

    public void setOldName(String oldName) {
        this.oldName = oldName;
    }

    public Integer getNextLevelId() {
        return nextLevelId;
    }

    public void setNextLevelId(Integer nextLevelId) {
        this.nextLevelId = nextLevelId;
    }

    public BigDecimal getRechargeMaxAmount() {
        return rechargeMaxAmount;
    }

    public void setRechargeMaxAmount(BigDecimal rechargeMaxAmount) {
        this.rechargeMaxAmount = rechargeMaxAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getRechargeSubAmount() {
        return rechargeSubAmount;
    }

    public void setRechargeSubAmount(BigDecimal rechargeSubAmount) {
        this.rechargeSubAmount = rechargeSubAmount;
    }

    public BigDecimal getRechargeMinAmount() {
        return rechargeMinAmount;
    }

    public void setRechargeMinAmount(BigDecimal rechargeMinAmount) {
        this.rechargeMinAmount = rechargeMinAmount;
    }

    /**
     * 获取主键ID
     *
     * @return id - 主键ID
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置主键ID
     *
     * @param id 主键ID
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取会员卡名称
     *
     * @return name - 会员卡名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置会员卡名称
     *
     * @param name 会员卡名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取续费金额
     *
     * @return renewal_amount - 续费金额
     */
    public BigDecimal getRenewalAmount() {
        return renewalAmount;
    }

    /**
     * 设置续费金额
     *
     * @param renewalAmount 续费金额
     */
    public void setRenewalAmount(BigDecimal renewalAmount) {
        this.renewalAmount = renewalAmount;
    }

    /**
     * 获取年限
     *
     * @return age_limit - 年限
     */
    public Integer getAgeLimit() {
        return ageLimit;
    }

    /**
     * 设置年限
     *
     * @param ageLimit 年限
     */
    public void setAgeLimit(Integer ageLimit) {
        this.ageLimit = ageLimit;
    }

    /**
     * 获取折扣率（价目表自动调价的折扣率）
     *
     * @return rate - 折扣率（价目表自动调价的折扣率）
     */
    public Float getRate() {
        return rate;
    }

    /**
     * 设置折扣率（价目表自动调价的折扣率）
     *
     * @param rate 折扣率（价目表自动调价的折扣率）
     */
    public void setRate(Float rate) {
        this.rate = rate;
    }

    /**
     * 获取类型,0:普通,1:VIP
     *
     * @return type - 类型,0:普通,1:VIP
     */
    public Byte getType() {
        return type;
    }

    /**
     * 设置类型,0:普通,1:VIP
     *
     * @param type 类型,0:普通,1:VIP
     */
    public void setType(Byte type) {
        this.type = type;
    }

    /**
     * 设置会员卡图标
     *
     * @return icon
     */
    public String getIcon() {
        return icon;
    }

    /**
     * 获取会员卡图标
     *
     * @param icon 图标
     */
    public void setIcon(String icon) {
        this.icon = icon;
    }

    /**
     * 获取会员卡图片码
     *
     * @return pictureCode
     */
    public String getPictureCode() {
        return pictureCode;
    }

    /**
     * 设置会员卡图片码
     *
     * @param pictureCode 会员卡图片码
     */
    public void setPictureCode(String pictureCode) {
        this.pictureCode = pictureCode;
    }

    /**
     * 获取会员卡描述（青藤、银藤、金藤、艾维会员）
     *
     * @return description - 会员卡描述（青藤、银藤、金藤、艾维会员）
     */
    public String getDescription() {
        return description;
    }

    /**
     * 设置会员卡描述（青藤、银藤、金藤、艾维会员）
     *
     * @param description 会员卡描述（青藤、银藤、金藤、艾维会员）
     */
    public void setDescription(String description) {
        this.description = description;
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
     * 获取创建人名称
     *
     * @return crt_name - 创建人名称
     */
    public String getCrtName() {
        return crtName;
    }

    /**
     * 设置创建人名称
     *
     * @param crtName 创建人名称
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
     * @return upd_id
     */
    public Integer getUpdId() {
        return updId;
    }

    /**
     * @param updId
     */
    public void setUpdId(Integer updId) {
        this.updId = updId;
    }

    /**
     * 获取修改人名称
     *
     * @return upd_name - 修改人名称
     */
    public String getUpdName() {
        return updName;
    }

    /**
     * 设置修改人名称
     *
     * @param updName 修改人名称
     */
    public void setUpdName(String updName) {
        this.updName = updName;
    }

    /**
     * 获取修改时间
     *
     * @return upd_time - 修改时间
     */
    public Date getUpdTime() {
        return updTime;
    }

    /**
     * 设置修改时间
     *
     * @param updTime 修改时间
     */
    public void setUpdTime(Date updTime) {
        this.updTime = updTime;
    }
}