package com.yunya.modules.system.vo.tree;


import com.yunya.framework.common.model.TreeNode;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Column;

/**
 * 简单介绍:</br> 组织树
 *
 * @author: chow
 * @date: 2020/6/4 20:38
 * @description:
 * @since: 1.0.0
 */
public class OrganizationTreeVO extends TreeNode {
  /** 组织名称 */
  private String name;
  /** 组织类型 */
  private String type;
  /** 自定义排序 */
  private Integer orderNum;
  /** 组织编号 */
  private String clinicNumber;
  /** 组织简称 */
  private String abbreviation;
  /** 组织品牌 */
  private String brandName;
  @ApiModelProperty("是否开启全诊通同步")
  private Boolean enableQztSync;
  @ApiModelProperty("全诊通医疗机构编码")
  private String qztInstitutionCode;
  @ApiModelProperty("滨江医疗机构编码")
  private Boolean enableBjSync;

  @ApiModelProperty("滨江医疗机构编码")
  private String bjInstitutionCode;
  private Boolean enableXhqSync;
  private String xhqInstitutionCode;

  public OrganizationTreeVO() {
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Integer getOrderNum() {
    return orderNum;
  }

  public void setOrderNum(Integer orderNum) {
    this.orderNum = orderNum;
  }

  public String getClinicNumber() {
    return clinicNumber;
  }

  public void setClinicNumber(String clinicNumber) {
    this.clinicNumber = clinicNumber;
  }

  public String getAbbreviation() {
    return abbreviation;
  }

  public void setAbbreviation(String abbreviation) {
    this.abbreviation = abbreviation;
  }

  public String getBrandName() {
    return brandName;
  }

  public void setBrandName(String brandName) {
    this.brandName = brandName;
  }

  public Boolean getEnableQztSync() {
    return enableQztSync;
  }

  public void setEnableQztSync(Boolean enableQztSync) {
    this.enableQztSync = enableQztSync;
  }

  public String getQztInstitutionCode() {
    return qztInstitutionCode;
  }

  public void setQztInstitutionCode(String qztInstitutionCode) {
    this.qztInstitutionCode = qztInstitutionCode;
  }

  public Boolean getEnableBjSync() {
    return enableBjSync;
  }

  public void setEnableBjSync(Boolean enableBjSync) {
    this.enableBjSync = enableBjSync;
  }

  public String getBjInstitutionCode() {
    return bjInstitutionCode;
  }

  public void setBjInstitutionCode(String bjInstitutionCode) {
    this.bjInstitutionCode = bjInstitutionCode;
  }

  public Boolean getEnableXhqSync() {
    return enableXhqSync;
  }

  public void setEnableXhqSync(Boolean enableXhqSync) {
    this.enableXhqSync = enableXhqSync;
  }

  public String getXhqInstitutionCode() {
    return xhqInstitutionCode;
  }

  public void setXhqInstitutionCode(String xhqInstitutionCode) {
    this.xhqInstitutionCode = xhqInstitutionCode;
  }

  @Override
  public String toString() {
    return "OrganizationTreeVO{" +
            "name='" + name + '\'' +
            ", type=" + type +
            ", orderNum=" + orderNum +
            ", clinicNumber='" + clinicNumber + '\'' +
            ", abbreviation='" + abbreviation + '\'' +
            ", brandName='" + brandName + '\'' +
            ", id=" + id +
            ", parentId=" + parentId +
            '}';
  }
}
