package com.yunya.models.system;


import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "company_department")
public class CompanyDepartment {
  @Id private Integer id;

  /** 公司父部门ID */
  @Column(name = "parent_id")
  private Integer parentId;

  /** 公司ID */
  @Column(name = "company_id")
  private Integer companyId;

  /** 部门模版ID */
  @Column(name = "department_id")
  private Integer departmentId;

  /** 组织部门排序 */
  @Column(name = "order_num")
  private Integer orderNum;

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

  /** 修改人名称 */
  @Column(name = "upd_name")
  private String updName;

  /** 修改时间 */
  @Column(name = "upd_time")
  private Date updTime;

  /** @return id */
  public Integer getId() {
    return id;
  }

  /** @param id */
  public void setId(Integer id) {
    this.id = id;
  }

  /**
   * 获取公司父部门ID
   *
   * @return parent_id - 公司父部门ID
   */
  public Integer getParentId() {
    return parentId;
  }

  /**
   * 设置公司父部门ID
   *
   * @param parentId 公司父部门ID
   */
  public void setParentId(Integer parentId) {
    this.parentId = parentId;
  }

  /**
   * 获取公司ID
   *
   * @return company_id - 公司ID
   */
  public Integer getCompanyId() {
    return companyId;
  }

  /**
   * 设置公司ID
   *
   * @param companyId 公司ID
   */
  public void setCompanyId(Integer companyId) {
    this.companyId = companyId;
  }

  /**
   * 获取部门模版ID
   *
   * @return department_id - 部门模版ID
   */
  public Integer getDepartmentId() {
    return departmentId;
  }

  /**
   * 设置部门模版ID
   *
   * @param departmentId 部门模版ID
   */
  public void setDepartmentId(Integer departmentId) {
    this.departmentId = departmentId;
  }

  public Integer getOrderNum() {
    return orderNum;
  }

  /**
   * 排序序号
   *
   * @param orderNum 自定义组织部门排序
   */
  public void setOrderNum(Integer orderNum) {
    this.orderNum = orderNum;
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
}
