package com.yunya.models.patient_central;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Table(name = "patient_transfer_record")
public class PatientTransferRecord {
    /**
     * 转账记录id
     */
    @Id
    @GeneratedValue(generator = "JDBC", strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 门诊id
     */
    @Column(name = "org_id")
    private Integer orgId;

    /**
     * 主体卡号
     */
    @Column(name = "main_number")
    private String mainNumber;

    /**
     * 次要卡号
     */
    @Column(name = "minor_number")
    private String minorNumber;

    /**
     * 类型：1-转入，2-转出
     */
    private Byte type;

    /**
     * 本金
     */
    private BigDecimal principal;

    /**
     * 赠金
     */
    private BigDecimal bonus;

    /**
     * 是否启用
     */
    private Boolean inservice;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    @Column(name = "crt_time")
    private Date crtTime;

    /**
     * 创建人id
     */
    @Column(name = "crt_id")
    private Integer crtId;

    /**
     * 获取转账记录id
     *
     * @return id - 转账记录id
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置转账记录id
     *
     * @param id 转账记录id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取门诊id
     *
     * @return org_id - 门诊id
     */
    public Integer getOrgId() {
        return orgId;
    }

    /**
     * 设置门诊id
     *
     * @param orgId 门诊id
     */
    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    /**
     * 获取主体卡号
     *
     * @return transferor_number - 主体卡号
     */
    public String getMainNumber() {
        return mainNumber;
    }

    /**
     * 设置主体卡号
     *
     * @param mainNumber 主体卡号
     */
    public void setMainNumber(String mainNumber) {
        this.mainNumber = mainNumber;
    }

    /**
     * 获取次要卡号
     *
     * @return acceptor_number - 次要卡号
     */
    public String getMinorNumber() {
        return minorNumber;
    }

    /**
     * 设置次要卡号
     *
     * @param minorNumber 次要卡号
     */
    public void setMinorNumber(String minorNumber) {
        this.minorNumber = minorNumber;
    }

    /**
     * 获取类型：1-转入，2-转出
     *
     * @return
     */
    public Byte getType() {
        return type;
    }

    /**
     * 设置类型：1-转入，2-转出
     *
     * @param type
     */
    public void setType(Byte type) {
        this.type = type;
    }

    /**
     * 获取本金
     *
     * @return principal - 本金
     */
    public BigDecimal getPrincipal() {
        return principal;
    }

    /**
     * 设置本金
     *
     * @param principal 本金
     */
    public void setPrincipal(BigDecimal principal) {
        this.principal = principal;
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
}