package com.yunya.models.emr;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "treat_plan_detail_writeoff")
public class TreatPlanDetailWriteoff {
    /**
     * 主键id
     */
    @Id
    private Integer id;

    /**
     * 治疗计划详情id
     */
    @Column(name = "plan_detail_id")
    private Integer planDetailId;

    /**
     * 订单详情id
     */
    @Column(name = "order_detail_id")
    private Integer orderDetailId;

    /**
     * 项目核销数量
     */
    @Column(name = "write_off_quantity")
    private Integer writeOffQuantity;

    /**
     * 状态：0-未核销；1-已核销；2-已终止
     */
    @Column(name = "状态：0-未核销；1-已核销；2-已终止")
    private Byte status;

    /**
     * 创建人id
     */
    @Column(name = "crt_id")
    private Integer crtId;

    /**
     * 创建时间
     */
    @Column(name = "crt_time")
    private Date crtTime;

    /**
     * 更新人id
     */
    @Column(name = "upt_id")
    private Integer uptId;

    /**
     * 更新时间
     */
    @Column(name = "upt_time")
    private Date uptTime;

    /**
     * 获取主键id
     *
     * @return id - 主键id
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置主键id
     *
     * @param id 主键id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取治疗计划详情id
     *
     * @return plan_detail_id - 治疗计划详情id
     */
    public Integer getPlanDetailId() {
        return planDetailId;
    }

    /**
     * 设置治疗计划详情id
     *
     * @param planDetailId 治疗计划详情id
     */
    public void setPlanDetailId(Integer planDetailId) {
        this.planDetailId = planDetailId;
    }

    /**
     * 获取订单详情id
     *
     * @return order_detail_id - 订单详情id
     */
    public Integer getOrderDetailId() {
        return orderDetailId;
    }

    /**
     * 设置订单详情id
     *
     * @param orderDetailId 订单详情id
     */
    public void setOrderDetailId(Integer orderDetailId) {
        this.orderDetailId = orderDetailId;
    }

    /**
     * 获取项目核销数量
     *
     * @return write_off_quantity - 项目核销数量
     */
    public Integer getWriteOffQuantity() {
        return writeOffQuantity;
    }

    /**
     * 设置项目核销数量
     *
     * @param writeOffQuantity 项目核销数量
     */
    public void setWriteOffQuantity(Integer writeOffQuantity) {
        this.writeOffQuantity = writeOffQuantity;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public Integer getUptId() {
        return uptId;
    }

    public void setUptId(Integer uptId) {
        this.uptId = uptId;
    }

    public Date getUptTime() {
        return uptTime;
    }

    public void setUptTime(Date uptTime) {
        this.uptTime = uptTime;
    }

    /**
     * 获取创建人id
     *
     * @return crt_id - 创建人id
     */
    public Integer getCrtId() {
        return crtId;
    }

    /**
     * 设置创建人id
     *
     * @param crtId 创建人id
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