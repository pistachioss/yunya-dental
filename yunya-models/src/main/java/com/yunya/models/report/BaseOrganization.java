package com.yunya.models.report;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "base_organization")
@ApiModel("查询门诊列表Vo")
public class BaseOrganization {
    /**
     * 组织ID
     */
    @Id
    @Column(name = "org_id")
    @ApiModelProperty(value = "组织ID")
    private Integer orgId;

    /**
     * 上级组织ID
     */
    @Column(name = "parent_id")
    @ApiModelProperty(value = "上级组织ID")
    private Integer parentId;

    /**
     * 组织类型（0-公司；1-区域；2-医疗机构；3-其他）
     */
    @Column(name = "org_type")
    @ApiModelProperty(value = "组织类型（0-公司；1-区域；2-医疗机构；3-其他）")
    private Byte orgType;

    /**
     * 组织简称
     */
    @ApiModelProperty(value = "组织简称")
    private String abbreviation;

    /**
     * 门诊编号
     */
    @Column(name = "clinic_number")
    @ApiModelProperty(value = "门诊编号")
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

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
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