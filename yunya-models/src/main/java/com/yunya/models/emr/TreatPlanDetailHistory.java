package com.yunya.models.emr;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Table(name = "treat_plan_detail_history")
public class TreatPlanDetailHistory {
    /**
     * 主键ID
     */
    @Id
    private Integer id;

    /**
     * 治疗计划-步骤明细id
     */
    @Column(name = "step_detail_id")
    private Integer stepDetailId;

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
     * 门诊项目单价
     */
    private BigDecimal price;

    /**
     * 数量
     */
    private Integer quantity;

    /**
     * 执行状态：1-未开始; 2-进行中; 3-全部完成; 4-提前终止
     */
    private Byte status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 写操作类型：0-新增，1-修改，2-删除
     */
    private Byte operation;

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
     * 获取治疗计划-步骤明细id
     *
     * @return step_detail_id - 治疗计划-步骤明细id
     */
    public Integer getStepDetailId() {
        return stepDetailId;
    }

    /**
     * 设置治疗计划-步骤明细id
     *
     * @param stepDetailId 治疗计划-步骤明细id
     */
    public void setStepDetailId(Integer stepDetailId) {
        this.stepDetailId = stepDetailId;
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
     * 获取执行状态：1-未开始; 2-进行中; 3-全部完成; 4-提前终止
     *
     * @return status - 执行状态：1-未开始; 2-进行中; 3-全部完成; 4-提前终止
     */
    public Byte getStatus() {
        return status;
    }

    /**
     * 设置执行状态：1-未开始; 2-进行中; 3-全部完成; 4-提前终止
     *
     * @param status 执行状态：1-未开始; 2-进行中; 3-全部完成; 4-提前终止
     */
    public void setStatus(Byte status) {
        this.status = status;
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
     * 获取写操作类型：0-新增，1-修改，2-删除
     *
     * @return operation - 写操作类型：0-新增，1-修改，2-删除
     */
    public Byte getOperation() {
        return operation;
    }

    /**
     * 设置写操作类型：0-新增，1-修改，2-删除
     *
     * @param operation 写操作类型：0-新增，1-修改，2-删除
     */
    public void setOperation(Byte operation) {
        this.operation = operation;
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
}