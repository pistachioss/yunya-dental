package com.yunya.models.discount;

import java.util.Date;
import javax.persistence.*;

@Table(name = "card_clinic")
public class CardClinic {
    /**
     * 主键
     */
    @Id
    private Integer id;

    /**
     * 优惠方式的ID
     */
    @Column(name = "relevance_id")
    private Integer relevanceId;

    /**
     * 门诊ID
     */
    @Column(name = "clinic_id")
    private Integer clinicId;

    /**
     * 部门ID
     */
    @Column(name = "department_id")
    private Integer departmentId;

    /**
     * 类型 0:代金券,1:折扣券,2:套餐券,4:充值卡
     */
    private Integer type;

    /**
     * 组织类型 0.门诊,1:公司
     */
    @Column(name = "org_type")
    private Integer orgType;

    /**
     * 数量
     */
    private Integer count;

    /**
     * 号段
     */
    @Column(name = "start_number")
    private String startNumber;

    /**
     * 0:分配计划中,1:完成分配
     */
    private Integer status;

    /**
     * 号段
     */
    @Column(name = "end_number")
    private String endNumber;

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
     * 获取门诊ID
     *
     * @return clinic_id - 门诊ID
     */
    public Integer getClinicId() {
        return clinicId;
    }

    /**
     * 设置门诊ID
     *
     * @param clinicId 门诊ID
     */
    public void setClinicId(Integer clinicId) {
        this.clinicId = clinicId;
    }

    /**
     * 获取部门ID
     *
     * @return department_id - 部门ID
     */
    public Integer getDepartmentId() {
        return departmentId;
    }

    /**
     * 设置部门ID
     *
     * @param departmentId 部门ID
     */
    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
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
     * 获取组织类型 0.门诊,1:公司
     *
     * @return org_type - 组织类型 0.门诊,1:公司
     */
    public Integer getOrgType() {
        return orgType;
    }

    /**
     * 设置组织类型 0.门诊,1:公司
     *
     * @param orgType 组织类型 0.门诊,1:公司
     */
    public void setOrgType(Integer orgType) {
        this.orgType = orgType;
    }

    /**
     * 获取数量
     *
     * @return count - 数量
     */
    public Integer getCount() {
        return count;
    }

    /**
     * 设置数量
     *
     * @param count 数量
     */
    public void setCount(Integer count) {
        this.count = count;
    }

    /**
     * 获取号段
     *
     * @return start_number - 号段
     */
    public String getStartNumber() {
        return startNumber;
    }

    /**
     * 设置号段
     *
     * @param startNumber 号段
     */
    public void setStartNumber(String startNumber) {
        this.startNumber = startNumber;
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
     * 获取号段
     *
     * @return end_number - 号段
     */
    public String getEndNumber() {
        return endNumber;
    }

    /**
     * 设置号段
     *
     * @param endNumber 号段
     */
    public void setEndNumber(String endNumber) {
        this.endNumber = endNumber;
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