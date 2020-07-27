package com.clinic.discount.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Table(name = "recharge_card")
@ApiModel("充值卡")
public class RechargeCard {
    /**
     * 主键
     */
    @Id
    @ApiModelProperty("主键")
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
    @ApiModelProperty("实物卡 0:非实体卡,1:实体卡")
    @NotNull(message = "是否实物卡不能为空")
    private Boolean physicalCard;

    /**
     * 营销产品分类ID
     */
    @Column(name = "market_product_type_id")
    @ApiModelProperty("营销产品分类ID")
    @NotNull(message = "营销产品分类ID不能为空")
    private Integer marketProductTypeId;

    /**
     * 面值
     */
    @Column(name = "face_value")
    @ApiModelProperty("面值")
    @NotNull(message = "面值不能为空")
    private BigDecimal faceValue;

    /**
     * 售价
     */
    @Column(name = "selling_price")
    @ApiModelProperty("售价")
    @NotNull(message = "售价不能为空")
    private BigDecimal sellingPrice;

    /**
     * 赠金
     */
    @ApiModelProperty("赠金")
    @NotNull(message = "赠金不能为空")
    private BigDecimal bonus;

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
     * 激活截至日期
     */
    @Column(name = "activation_deadline")
    @ApiModelProperty("激活截至日期")
    private Date activationDeadline;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String remark;

    /**
     * 可使用门诊
     */
    @Column(name = "clinic_ids")
    @ApiModelProperty("可使用门诊")
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
     * 获取赠金
     *
     * @return bonus - 赠金
     */
    public BigDecimal getBonus() {
        return bonus;
    }

    /**
     * 设置赠金
     *
     * @param bonus 赠金
     */
    public void setBonus(BigDecimal bonus) {
        this.bonus = bonus;
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
     * 获取激活截至日期
     *
     * @return activation_deadline - 激活截至日期
     */
    public Date getActivationDeadline() {
        return activationDeadline;
    }

    /**
     * 设置激活截至日期
     *
     * @param activationDeadline 激活截至日期
     */
    public void setActivationDeadline(Date activationDeadline) {
        this.activationDeadline = activationDeadline;
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