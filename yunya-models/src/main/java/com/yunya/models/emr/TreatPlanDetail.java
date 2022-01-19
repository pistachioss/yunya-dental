package com.yunya.models.emr;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Table(name = "treat_plan_detail")
public class TreatPlanDetail {
    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    /**
     * 治疗计划ID
     */
    @Column(name = "treat_plan_id")
    private Integer treatPlanId;

    /**
     * 治疗计划步骤ID
     */
    @Column(name = "treat_step_id")
    private Integer treatStepId;

    /**
     * 开单类型（0-价目表；1-商品；）
     */
    private Byte type;

    /**
     * 牙位
     */
    @Column(name = "tooth_bit")
    private String toothBit;

    /**
     * 项目ID
     */
    @Column(name = "billing_item_id")
    private Integer billingItemId;

    /**
     * 项目名称
     */
    @Column(name = "billing_item_name")
    private String billingItemName;

    /**
     * 单位
     */
    private String unit;

    /**
     * 门诊项目单价
     */
    private BigDecimal price;

    /**
     * 数量
     */
    private Integer quantity;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建人ID
     */
    @Column(name = "crt_id")
    private Integer crtId;

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
     * 更新时间
     */
    @Column(name = "upd_time")
    private Date updTime;

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
     * 获取治疗计划ID
     *
     * @return treat_plan_id - 治疗计划ID
     */
    public Integer getTreatPlanId() {
        return treatPlanId;
    }

    /**
     * 设置治疗计划ID
     *
     * @param treatPlanId 治疗计划ID
     */
    public void setTreatPlanId(Integer treatPlanId) {
        this.treatPlanId = treatPlanId;
    }

    /**
     * 获取治疗计划步骤ID
     *
     * @return treat_step_id - 治疗计划步骤ID
     */
    public Integer getTreatStepId() {
        return treatStepId;
    }

    /**
     * 设置治疗计划步骤ID
     *
     * @param treatStepId 治疗计划步骤ID
     */
    public void setTreatStepId(Integer treatStepId) {
        this.treatStepId = treatStepId;
    }

    /**
     * 获取开单类型（0-价目表；1-商品；）
     *
     * @return type - 开单类型（0-价目表；1-商品；）
     */
    public Byte getType() {
        return type;
    }

    /**
     * 设置开单类型（0-价目表；1-商品；）
     *
     * @param type 开单类型（0-价目表；1-商品；）
     */
    public void setType(Byte type) {
        this.type = type;
    }

    /**
     * 获取牙位
     *
     * @return tooth_bit - 牙位
     */
    public String getToothBit() {
        return toothBit;
    }

    /**
     * 设置牙位
     *
     * @param toothBit 牙位
     */
    public void setToothBit(String toothBit) {
        this.toothBit = toothBit;
    }

    /**
     * 获取项目ID
     *
     * @return billing_item_id - 项目ID
     */
    public Integer getBillingItemId() {
        return billingItemId;
    }

    /**
     * 设置项目ID
     *
     * @param billingItemId 项目ID
     */
    public void setBillingItemId(Integer billingItemId) {
        this.billingItemId = billingItemId;
    }

    /**
     * 获取项目名称
     *
     * @return billing_item_name - 项目名称
     */
    public String getBillingItemName() {
        return billingItemName;
    }

    /**
     * 设置项目名称
     *
     * @param billingItemName 项目名称
     */
    public void setBillingItemName(String billingItemName) {
        this.billingItemName = billingItemName;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    /**
     * 获取门诊项目单价
     *
     * @return price - 门诊项目单价
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * 设置门诊项目单价
     *
     * @param price 门诊项目单价
     */
    public void setPrice(BigDecimal price) {
        this.price = price;
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