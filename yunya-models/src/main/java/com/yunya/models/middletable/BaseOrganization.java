package com.yunya.models.middletable;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "base_organization")
public class BaseOrganization {
    /**
     * 组织ID
     */
    @Id
    @Column(name = "org_id")
    private Integer orgId;

    /**
     * 组织类型（0-公司；1-区域；2-医疗机构；3-其他）
     */
    @Column(name = "org_type")
    private Byte orgType;

    /**
     * 组织简称
     */
    private String abbreviation;

    /**
     * 门诊编号
     */
    @Column(name = "clinic_number")
    private String clinicNumber;

    /**
     * 获取组织ID
     *
     * @return org_id - 组织ID
     */
    public Integer getOrgId() {
        return orgId;
    }

    /**
     * 设置组织ID
     *
     * @param orgId 组织ID
     */
    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    /**
     * 获取组织类型（0-公司；1-区域；2-医疗机构；3-其他）
     *
     * @return org_type - 组织类型（0-公司；1-区域；2-医疗机构；3-其他）
     */
    public Byte getOrgType() {
        return orgType;
    }

    /**
     * 设置组织类型（0-公司；1-区域；2-医疗机构；3-其他）
     *
     * @param orgType 组织类型（0-公司；1-区域；2-医疗机构；3-其他）
     */
    public void setOrgType(Byte orgType) {
        this.orgType = orgType;
    }

    /**
     * 获取组织简称
     *
     * @return abbreviation - 组织简称
     */
    public String getAbbreviation() {
        return abbreviation;
    }

    /**
     * 设置组织简称
     *
     * @param abbreviation 组织简称
     */
    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    /**
     * 获取门诊编号
     *
     * @return clinic_number - 门诊编号
     */
    public String getClinicNumber() {
        return clinicNumber;
    }

    /**
     * 设置门诊编号
     *
     * @param clinicNumber 门诊编号
     */
    public void setClinicNumber(String clinicNumber) {
        this.clinicNumber = clinicNumber;
    }
}