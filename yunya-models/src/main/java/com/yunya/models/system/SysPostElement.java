package com.yunya.models.system;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "sys_post_element")
public class SysPostElement {
  @Id private Integer id;

  /** 岗位ID */
  @Column(name = "post_id")
  private Integer postId;

  /** 系统按钮ID */
  @Column(name = "sys_element_id")
  private String sysElementId;

  /** 权限类型 */
  private Byte type;

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
   * 获取岗位ID
   *
   * @return post_id - 岗位ID
   */
  public Integer getPostId() {
    return postId;
  }

  /**
   * 设置岗位ID
   *
   * @param postId 岗位ID
   */
  public void setPostId(Integer postId) {
    this.postId = postId;
  }

  /**
   * 获取系统按钮ID
   *
   * @return sys_element_id - 系统按钮ID
   */
  public String getSysElementId() {
    return sysElementId;
  }

  /**
   * 设置系统按钮ID
   *
   * @param sysElementId 系统按钮ID
   */
  public void setSysElementId(String sysElementId) {
    this.sysElementId = sysElementId;
  }

  /**
   * 获取权限类型
   *
   * @return
   */
  public Byte getType() {
    return type;
  }

  /**
   * 设置权限类型
   *
   * @param type 权限类型
   */
  public void setType(Byte type) {
    this.type = type;
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
