package com.yunya.modules.system.entity;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "clinic_ext_info")
public class ClinicExtInfo {
  /** 主键ID */
  @Id private Integer id;

  /** 组织机构ID */
  @Column(name = "company_id")
  private Integer companyId;

  /** 公司品牌ID列表 */
  @Column(name = "brand_ids")
  private String brandIds;

  /** 门诊编号 */
  @Column(name = "clinic_number")
  private String clinicNumber;

  /** 诊所简称 */
  private String abbreviation;

  /** 诊所电话 */
  private String tel;

  /** 诊所传真 */
  private String fax;

  /** 牙椅数量 */
  @Column(name = "chair_quantity")
  private Integer chairQuantity;

  /** 营业开始时间 */
  @Column(name = "business_start_time")
  private String businessStartTime;

  /** 营业结束时间 */
  @Column(name = "business_end_time")
  private String businessEndTime;

  /** 地址省 */
  @Column(name = "addr_province")
  private String addrProvince;

  /** 地址市 */
  @Column(name = "addr_city")
  private String addrCity;

  /** 地址区 */
  @Column(name = "addr_region")
  private String addrRegion;

  /** 详细地址 */
  private String address;

  /** 医疗机构图片地址 */
  private String path;

  /** 是否启用 */
  private Boolean inservice;

  /** 创建人ID */
  @Column(name = "crt_id")
  private Integer crtId;

  /** 创建人名称 */
  @Column(name = "crt_name")
  private String crtName;

  /** 创建时间 */
  @Column(name = "crt_time")
  private Date crtTime;

  @Column(name = "upd_id")
  private Integer updId;

  /** 修改时间 */
  @Column(name = "upd_time")
  private Date updTime;

  /** 修改人名称 */
  @Column(name = "upd_name")
  private String updName;

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
   * 获取组织机构ID
   *
   * @return company_id - 组织机构ID
   */
  public Integer getCompanyId() {
    return companyId;
  }

  /**
   * 设置组织机构ID
   *
   * @param companyId 组织机构ID
   */
  public void setCompanyId(Integer companyId) {
    this.companyId = companyId;
  }

  /**
   * 获取公司品牌ID列表
   *
   * @return brand_ids - 公司品牌ID列表
   */
  public String getBrandIds() {
    return brandIds;
  }

  /**
   * 设置公司品牌ID列表
   *
   * @param brandIds 公司品牌ID列表
   */
  public void setBrandIds(String brandIds) {
    this.brandIds = brandIds;
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

  /**
   * 获取诊所简称
   *
   * @return abbreviation - 诊所简称
   */
  public String getAbbreviation() {
    return abbreviation;
  }

  /**
   * 设置诊所简称
   *
   * @param abbreviation 诊所简称
   */
  public void setAbbreviation(String abbreviation) {
    this.abbreviation = abbreviation;
  }

  /**
   * 获取诊所电话
   *
   * @return tel - 诊所电话
   */
  public String getTel() {
    return tel;
  }

  /**
   * 设置诊所电话
   *
   * @param tel 诊所电话
   */
  public void setTel(String tel) {
    this.tel = tel;
  }

  /**
   * 获取诊所传真
   *
   * @return fax - 诊所传真
   */
  public String getFax() {
    return fax;
  }

  /**
   * 设置诊所传真
   *
   * @param fax 诊所传真
   */
  public void setFax(String fax) {
    this.fax = fax;
  }

  /**
   * 获取牙椅数量
   *
   * @return chair_quantity - 牙椅数量
   */
  public Integer getChairQuantity() {
    return chairQuantity;
  }

  /**
   * 设置牙椅数量
   *
   * @param chairQuantity 牙椅数量
   */
  public void setChairQuantity(Integer chairQuantity) {
    this.chairQuantity = chairQuantity;
  }

  /**
   * 获取营业开始时间
   *
   * @return business_start_time - 营业开始时间
   */
  public String getBusinessStartTime() {
    return businessStartTime;
  }

  /**
   * 设置营业开始时间
   *
   * @param businessStartTime 营业开始时间
   */
  public void setBusinessStartTime(String businessStartTime) {
    this.businessStartTime = businessStartTime;
  }

  /**
   * 获取营业结束时间
   *
   * @return business_end_time - 营业结束时间
   */
  public String getBusinessEndTime() {
    return businessEndTime;
  }

  /**
   * 设置营业结束时间
   *
   * @param businessEndTime 营业结束时间
   */
  public void setBusinessEndTime(String businessEndTime) {
    this.businessEndTime = businessEndTime;
  }

  /**
   * 获取地址省
   *
   * @return addr_province - 地址省
   */
  public String getAddrProvince() {
    return addrProvince;
  }

  /**
   * 设置地址省
   *
   * @param addrProvince 地址省
   */
  public void setAddrProvince(String addrProvince) {
    this.addrProvince = addrProvince;
  }

  /**
   * 获取地址市
   *
   * @return addr_city - 地址市
   */
  public String getAddrCity() {
    return addrCity;
  }

  /**
   * 设置地址市
   *
   * @param addrCity 地址市
   */
  public void setAddrCity(String addrCity) {
    this.addrCity = addrCity;
  }

  /**
   * 获取地址区
   *
   * @return addr_region - 地址区
   */
  public String getAddrRegion() {
    return addrRegion;
  }

  /**
   * 设置地址区
   *
   * @param addrRegion 地址区
   */
  public void setAddrRegion(String addrRegion) {
    this.addrRegion = addrRegion;
  }

  /**
   * 获取详细地址
   *
   * @return address - 详细地址
   */
  public String getAddress() {
    return address;
  }

  /**
   * 设置详细地址
   *
   * @param address 详细地址
   */
  public void setAddress(String address) {
    this.address = address;
  }

  /**
   * 设置医疗机构图片地址
   *
   * @return path - 医疗机构图片地址
   */
  public String getPath() {
    return path;
  }

  /**
   * 获取医疗机构图片地址
   *
   * @param path 医疗机构图片地址
   */
  public void setPath(String path) {
    this.path = path;
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
   * 获取创建人名称
   *
   * @return crt_name - 创建人名称
   */
  public String getCrtName() {
    return crtName;
  }

  /**
   * 设置创建人名称
   *
   * @param crtName 创建人名称
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

  /** @return upd_id */
  public Integer getUpdId() {
    return updId;
  }

  /** @param updId */
  public void setUpdId(Integer updId) {
    this.updId = updId;
  }

  /**
   * 获取修改时间
   *
   * @return upd_time - 修改时间
   */
  public Date getUpdTime() {
    return updTime;
  }

  /**
   * 设置修改时间
   *
   * @param updTime 修改时间
   */
  public void setUpdTime(Date updTime) {
    this.updTime = updTime;
  }

  /**
   * 获取修改人名称
   *
   * @return upd_name - 修改人名称
   */
  public String getUpdName() {
    return updName;
  }

  /**
   * 设置修改人名称
   *
   * @param updName 修改人名称
   */
  public void setUpdName(String updName) {
    this.updName = updName;
  }
}
