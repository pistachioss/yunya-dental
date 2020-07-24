package com.clinic.discount.entity;

import java.util.Date;
import javax.persistence.*;

@Table(name = "card_statistics")
public class CardStatistics {
    @Id
    private Integer id;

    /**
     * 优惠方式的ID
     */
    @Column(name = "relevance_id")
    private Integer relevanceId;

    /**
     * 名称
     */
    @Column(name = "relevance_name")
    private String relevanceName;

    /**
     * 0:分配计划中,1:完成分配
     */
    private Integer status;

    /**
     * 类型 0:代金券,1:折扣券,2:套餐券,4:充值卡
     */
    private Integer type;

    /**
     * 配给数量
     */
    private Integer count;

    /**
     * 售出数量
     */
    @Column(name = "selling_count")
    private Integer sellingCount;

    /**
     * 配给时间
     */
    @Column(name = "rationing_date")
    private Date rationingDate;

    /**
     * 配给人名称
     */
    @Column(name = "executor_name")
    private String executorName;

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
     * 批次
     */
    private Integer revision;

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
     * 获取优惠方式的ID
     *
     * @return relevance_id - 优惠方式的ID
     */
    public Integer getRelevanceId() {
        return relevanceId;
    }

    /**
     * 设置优惠方式的ID
     *
     * @param relevanceId 优惠方式的ID
     */
    public void setRelevanceId(Integer relevanceId) {
        this.relevanceId = relevanceId;
    }

    /**
     * 获取名称
     *
     * @return relevance_name - 名称
     */
    public String getRelevanceName() {
        return relevanceName;
    }

    /**
     * 设置名称
     *
     * @param relevanceName 名称
     */
    public void setRelevanceName(String relevanceName) {
        this.relevanceName = relevanceName;
    }

    /**
     * 获取0:分配计划中,1:完成分配
     *
     * @return status - 0:分配计划中,1:完成分配
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 设置0:分配计划中,1:完成分配
     *
     * @param status 0:分配计划中,1:完成分配
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * 获取类型 0:代金券,1:折扣券,2:套餐券,4:充值卡
     *
     * @return type - 类型 0:代金券,1:折扣券,2:套餐券,4:充值卡
     */
    public Integer getType() {
        return type;
    }

    /**
     * 设置类型 0:代金券,1:折扣券,2:套餐券,4:充值卡
     *
     * @param type 类型 0:代金券,1:折扣券,2:套餐券,4:充值卡
     */
    public void setType(Integer type) {
        this.type = type;
    }

    /**
     * 获取配给数量
     *
     * @return count - 配给数量
     */
    public Integer getCount() {
        return count;
    }

    /**
     * 设置配给数量
     *
     * @param count 配给数量
     */
    public void setCount(Integer count) {
        this.count = count;
    }

    /**
     * 获取售出数量
     *
     * @return selling_count - 售出数量
     */
    public Integer getSellingCount() {
        return sellingCount;
    }

    /**
     * 设置售出数量
     *
     * @param sellingCount 售出数量
     */
    public void setSellingCount(Integer sellingCount) {
        this.sellingCount = sellingCount;
    }

    /**
     * 获取配给时间
     *
     * @return rationing_date - 配给时间
     */
    public Date getRationingDate() {
        return rationingDate;
    }

    /**
     * 设置配给时间
     *
     * @param rationingDate 配给时间
     */
    public void setRationingDate(Date rationingDate) {
        this.rationingDate = rationingDate;
    }

    /**
     * 获取配给人名称
     *
     * @return executor_name - 配给人名称
     */
    public String getExecutorName() {
        return executorName;
    }

    /**
     * 设置配给人名称
     *
     * @param executorName 配给人名称
     */
    public void setExecutorName(String executorName) {
        this.executorName = executorName;
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
     * 获取批次
     *
     * @return revision - 批次
     */
    public Integer getRevision() {
        return revision;
    }

    /**
     * 设置批次
     *
     * @param revision 批次
     */
    public void setRevision(Integer revision) {
        this.revision = revision;
    }
}