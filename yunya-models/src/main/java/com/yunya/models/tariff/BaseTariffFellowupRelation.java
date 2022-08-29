package com.yunya.models.tariff;

import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @Class BaseTariffFellowupRelation
 * @Description
 * @Author lihuibin
 * @Date 2022/8/19 22:30
 * @Version 1.0
 */
@EqualsAndHashCode
@Table(name = "base_tariff_fellowup_relation")
public class BaseTariffFellowupRelation {
    /** 主键 */
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    /** 基础项目ID */
    @Column(name = "base_tariff_id")
    private Integer baseTariffId;

    /**
     * 几天后随访
     */
    @Column(name = "fellow_up")
    private Integer fellowUp;

    /** 随访原因 */
    @Column(name = "fellow_up_case")
    private String fellowUpCase;

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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getBaseTariffId() {
        return baseTariffId;
    }

    public void setBaseTariffId(Integer baseTariffId) {
        this.baseTariffId = baseTariffId;
    }

    public Integer getFellowUp() {
        return fellowUp;
    }

    public void setFellowUp(Integer fellowUp) {
        this.fellowUp = fellowUp;
    }

    public String getFellowUpCase() {
        return fellowUpCase;
    }

    public void setFellowUpCase(String fellowUpCase) {
        this.fellowUpCase = fellowUpCase;
    }

    public Boolean getInservice() {
        return inservice;
    }

    public void setInservice(Boolean inservice) {
        this.inservice = inservice;
    }

    public Integer getCrtId() {
        return crtId;
    }

    public void setCrtId(Integer crtId) {
        this.crtId = crtId;
    }

    public String getCrtName() {
        return crtName;
    }

    public void setCrtName(String crtName) {
        this.crtName = crtName;
    }

    public Date getCrtTime() {
        return crtTime;
    }

    public void setCrtTime(Date crtTime) {
        this.crtTime = crtTime;
    }

    public Integer getUpdId() {
        return updId;
    }

    public void setUpdId(Integer updId) {
        this.updId = updId;
    }

    public String getUpdName() {
        return updName;
    }

    public void setUpdName(String updName) {
        this.updName = updName;
    }

    public Date getUpdTime() {
        return updTime;
    }

    public void setUpdTime(Date updTime) {
        this.updTime = updTime;
    }

    @Override
    public String toString() {
        return "BaseTariffFellowupRelation{" +
                "id=" + id +
                ", baseTariffId=" + baseTariffId +
                ", fellowUp=" + fellowUp +
                ", fellowUpCase='" + fellowUpCase + '\'' +
                ", inservice=" + inservice +
                ", crtId=" + crtId +
                ", crtName='" + crtName + '\'' +
                ", crtTime=" + crtTime +
                ", updId=" + updId +
                ", updName='" + updName + '\'' +
                ", updTime=" + updTime +
                '}';
    }
}
