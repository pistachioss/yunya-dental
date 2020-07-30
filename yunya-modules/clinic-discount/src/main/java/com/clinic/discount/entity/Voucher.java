package com.clinic.discount.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

@ApiModel("代金券")
public class Voucher {
    /**
     * 主键
     */
    @Id
    @ApiModelProperty("主键ID")
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    /**
     * 名称
     */
    @ApiModelProperty("名称")
    @NotBlank(message = "名称不能为空")
    private String name;

    /**
     * 实物卡 0:非实体卡,1:实体卡
     */
    @Column(name = "physical_card")
    @ApiModelProperty("是否实物卡")
    @NotNull(message = "是否实物卡不能为空")
    private Boolean physicalCard;

    /**
     * 营销产品分类ID
     */
    @Column(name = "market_product_type_id")
    @NotNull(message = "营销产品分类ID不能为空")
    @ApiModelProperty("产品分类ID")
    private Integer marketProductTypeId;

    /**
     * 面值
     */
    @Column(name = "face_value")
    @NotNull(message = "面值不能为空")
    @ApiModelProperty("面值")
    private BigDecimal faceValue;

    /**
     * 售价
     */
    @Column(name = "selling_price")
    @NotNull(message = "售价不能为空")
    @ApiModelProperty("售价")
    private BigDecimal sellingPrice;

    /**
     * 图像
     */
    @ApiModelProperty("图像")
    private String icon;

    /**
     * 文档
     */
    @ApiModelProperty("文档")
    private String doc;

    /**
     * 售出开始日期
     */
    @Column(name = "selling_start_date")
    @ApiModelProperty("售出开始日期")
    private Date sellingStartDate;

    /**
     * 售出结束日期
     */
    @Column(name = "selling_end_date")
    @ApiModelProperty("售出结束日期")
    private Date sellingEndDate;

    /**
     * 激活截至日期(产品有效期)
     */
    @Column(name = "activation_deadline")
    @ApiModelProperty("产品有效期")
    private Date activationDeadline;

    /**
     * 激活后有效期
     */
    @Column(name = "effective_days")
    @ApiModelProperty("激活后有效期")
    private Integer effectiveDays;

    /**
     * 账单单次使用限制数量
     */
    @Column(name = "limit_count")
    @NotNull(message = "单次限制数量不能为空")
    @ApiModelProperty("账单单词使用限制数量")
    private Integer limitCount;

    /**
     * 是否可混合使用优惠 0.可以混合使用1.跟会员卡混合使用2.无法混合使用
     */
    @NotNull(message = "是否混合使用选项不能为空")
    private Integer mixable;

    /**
     * 是否可与他人共享
     */
    @NotNull(message = "可否与他人共享选项不能为空")
    @ApiModelProperty("是否与他人共享")
    private Boolean shareable;

    /**
     * 工作量比例
     */
    @Column(name = "workload_rate")
    @ApiModelProperty("工作量比例")
    private Integer workloadRate;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String remark;

    /**
     * 可使用门诊
     */
    @Column(name = "clinic_ids")
    @ApiModelProperty("可使用门诊列表")
    private String clinicIds;

    /**
     * 创建人
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
     * 更新人
     */
    @Column(name = "upd_id")
    private Integer updId;

    /**
     * 更新时间
     */
    @Column(name = "upd_name")
    private String updName;

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
     * 获取名称
     *
     * @return name - 名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置名称
     *
     * @param name 名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取实物卡 0:非实体卡,1:实体卡
     *
     * @return physical_card - 实物卡 0:非实体卡,1:实体卡
     */
    public Boolean getPhysicalCard() {
        return physicalCard;
    }

    /**
     * 设置实物卡 0:非实体卡,1:实体卡
     *
     * @param physicalCard 实物卡 0:非实体卡,1:实体卡
     */
    public void setPhysicalCard(Boolean physicalCard) {
        this.physicalCard = physicalCard;
    }

    /**
     * 获取营销产品分类ID
     *
     * @return market_product_type_id - 营销产品分类ID
     */
    public Integer getMarketProductTypeId() {
        return marketProductTypeId;
    }

    /**
     * 设置营销产品分类ID
     *
     * @param marketProductTypeId 营销产品分类ID
     */
    public void setMarketProductTypeId(Integer marketProductTypeId) {
        this.marketProductTypeId = marketProductTypeId;
    }

    /**
     * 获取面值
     *
     * @return face_value - 面值
     */
    public BigDecimal getFaceValue() {
        return faceValue;
    }

    /**
     * 设置面值
     *
     * @param faceValue 面值
     */
    public void setFaceValue(BigDecimal faceValue) {
        this.faceValue = faceValue;
    }

    /**
     * 获取售价
     *
     * @return selling_price - 售价
     */
    public BigDecimal getSellingPrice() {
        return sellingPrice;
    }

    /**
     * 设置售价
     *
     * @param sellingPrice 售价
     */
    public void setSellingPrice(BigDecimal sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    /**
     * 获取图像
     *
     * @return icon - 图像
     */
    public String getIcon() {
        return icon;
    }

    /**
     * 设置图像
     *
     * @param icon 图像
     */
    public void setIcon(String icon) {
        this.icon = icon;
    }

    /**
     * 获取文档
     *
     * @return doc - 文档
     */
    public String getDoc() {
        return doc;
    }

    /**
     * 设置文档
     *
     * @param doc 文档
     */
    public void setDoc(String doc) {
        this.doc = doc;
    }

    /**
     * 获取售出开始日期
     *
     * @return selling_start_date - 售出开始日期
     */
    public Date getSellingStartDate() {
        return sellingStartDate;
    }

    /**
     * 设置售出开始日期
     *
     * @param sellingStartDate 售出开始日期
     */
    public void setSellingStartDate(Date sellingStartDate) {
        this.sellingStartDate = sellingStartDate;
    }

    /**
     * 获取售出结束日期
     *
     * @return selling_end_date - 售出结束日期
     */
    public Date getSellingEndDate() {
        return sellingEndDate;
    }

    /**
     * 设置售出结束日期
     *
     * @param sellingEndDate 售出结束日期
     */
    public void setSellingEndDate(Date sellingEndDate) {
        this.sellingEndDate = sellingEndDate;
    }

    /**
     * 获取激活截至日期(产品有效期)
     *
     * @return activation_deadline - 激活截至日期(产品有效期)
     */
    public Date getActivationDeadline() {
        return activationDeadline;
    }

    /**
     * 设置激活截至日期(产品有效期)
     *
     * @param activationDeadline 激活截至日期(产品有效期)
     */
    public void setActivationDeadline(Date activationDeadline) {
        this.activationDeadline = activationDeadline;
    }

    /**
     * 获取激活后有效期
     *
     * @return effective_days - 激活后有效期
     */
    public Integer getEffectiveDays() {
        return effectiveDays;
    }

    /**
     * 设置激活后有效期
     *
     * @param effectiveDays 激活后有效期
     */
    public void setEffectiveDays(Integer effectiveDays) {
        this.effectiveDays = effectiveDays;
    }

    /**
     * 获取账单单次使用限制数量
     *
     * @return limit_count - 账单单次使用限制数量
     */
    public Integer getLimitCount() {
        return limitCount;
    }

    /**
     * 设置账单单次使用限制数量
     *
     * @param limitCount 账单单次使用限制数量
     */
    public void setLimitCount(Integer limitCount) {
        this.limitCount = limitCount;
    }

    /**
     * 获取是否可混合使用优惠 0.可以混合使用1.跟会员卡混合使用2.无法混合使用
     *
     * @return mixable - 是否可混合使用优惠 0.可以混合使用1.跟会员卡混合使用2.无法混合使用
     */
    public Integer getMixable() {
        return mixable;
    }

    /**
     * 设置是否可混合使用优惠 0.可以混合使用1.跟会员卡混合使用2.无法混合使用
     *
     * @param mixable 是否可混合使用优惠 0.可以混合使用1.跟会员卡混合使用2.无法混合使用
     */
    public void setMixable(Integer mixable) {
        this.mixable = mixable;
    }

    /**
     * 获取是否可与他人共享
     *
     * @return shareable - 是否可与他人共享
     */
    public Boolean getShareable() {
        return shareable;
    }

    /**
     * 设置是否可与他人共享
     *
     * @param shareable 是否可与他人共享
     */
    public void setShareable(Boolean shareable) {
        this.shareable = shareable;
    }

    /**
     * 获取工作量比例
     *
     * @return workload_rate - 工作量比例
     */
    public Integer getWorkloadRate() {
        return workloadRate;
    }

    /**
     * 设置工作量比例
     *
     * @param workloadRate 工作量比例
     */
    public void setWorkloadRate(Integer workloadRate) {
        this.workloadRate = workloadRate;
    }

    /**
     * 获取备注
     *
     * @return remark - 备注
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置备注
     *
     * @param remark 备注
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * 获取可使用门诊
     *
     * @return clinic_ids - 可使用门诊
     */
    public String getClinicIds() {
        return clinicIds;
    }

    /**
     * 设置可使用门诊
     *
     * @param clinicIds 可使用门诊
     */
    public void setClinicIds(String clinicIds) {
        this.clinicIds = clinicIds;
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
     * @return upd_name - 更新时间
     */
    public String getUpdName() {
        return updName;
    }

    /**
     * 设置更新时间
     *
     * @param updName 更新时间
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