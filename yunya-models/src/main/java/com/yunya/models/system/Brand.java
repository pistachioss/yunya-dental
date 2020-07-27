package com.yunya.models.system;

import javax.persistence.Column;
import javax.persistence.Id;
import java.util.Date;

public class Brand {
  @Id private Integer id;

  /** 品牌 */
  private String name;

  /** 自定义排序 */
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

  /** 修改时间 */
  @Column(name = "upd_time")
  private Date updTime;

  /** 修改人名称 */
  @Column(name = "upd_name")
  private String updName;

  /** @return id */
  public Integer getId() {
    return id;
  }

  /** @param id */
  public void setId(Integer id) {
    this.id = id;
  }

  /**
   * 获取品牌
   *
   * @return name - 品牌
   */
  public String getName() {
    return name;
  }

  /**
   * 设置品牌
   *
   * @param name 品牌
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
