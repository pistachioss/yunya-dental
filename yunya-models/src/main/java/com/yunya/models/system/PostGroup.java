package com.yunya.models.system;


import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "post_group")
public class PostGroup {
  /** 主键 */
  @Id
  private Integer id;

  /** 上级岗位分类ID */
  @Column(name = "parent_id")
  private Integer parentId;

  /** 岗位分类名称 */
  private String name;

  /** 自定义排序 */
  @Column(name = "order_num")
  private Integer orderNum;

  /** 允许操作（编辑/删除） */
  @Column(name = "allow_operation")
  private Boolean allowOperation;

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
   * 获取上级岗位分类ID
   *
   * @return parent_id - 上级岗位分类ID
   */
  public Integer getParentId() {
    return parentId;
  }

  /**
   * 设置上级岗位分类ID
   *
   * @param parentId 上级岗位分类ID
   */
  public void setParentId(Integer parentId) {
    this.parentId = parentId;
  }

  /**
   * 获取岗位分类名称
   *
   * @return name - 岗位分类名称
   */
  public String getName() {
    return name;
  }

  /**
   * 设置岗位分类名称
   *
   * @param name 岗位分类名称
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * 获取自定义排序
   *
   * @return order_num - 自定义排序
   */
  public Integer getOrderNum() {
    return orderNum;
  }

  /**
   * 设置自定义排序
   *
   * @param orderNum 自定义排序
   */
  public void setOrderNum(Integer orderNum) {
    this.orderNum = orderNum;
  }

  /**
   * 获取允许操作（编辑/删除）
   *
   * @return allowOperation
   */
  public Boolean getAllowOperation() {
    return allowOperation;
  }

  /**
   * 设置允许被操纵
   *
   * @param allowOperation 获取允许操作（编辑/删除）
   */
  public void setAllowOperation(Boolean allowOperation) {
    this.allowOperation = allowOperation;
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
