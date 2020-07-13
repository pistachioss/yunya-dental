package com.yunya.modules.system.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@ApiModel("部门模版新增参数模型")
public class Department {
  @Id
  @ApiModelProperty(hidden = true)
  private Integer id;

  /** 部门名 */
  @ApiModelProperty(value = "部门名称", required = true)
  @Size(max = 50, message = "名称长度不能超过50个字符")
  @NotBlank(message = "部门名称不能为空！")
  private String name;

  /** 部门类型(0-门诊部门；1-公司部门） */
  @ApiModelProperty(value = "部门类型（0-门诊部门；1-公司部门）", required = true)
  @NotNull(message = "部门类型不能为空！")
  private Byte type;

  /** 自定义排序 */
  @Column(name = "order_num")
  @ApiModelProperty("自定义排序")
  private Integer orderNum;

  /** 是否启用 */
  @ApiModelProperty("是否启用")
  private Boolean inservice;

  /** 创建人ID */
  @Column(name = "crt_id")
  @ApiModelProperty(hidden = true)
  private Integer crtId;

  /** 创建人名称 */
  @Column(name = "crt_name")
  @ApiModelProperty(hidden = true)
  private String crtName;

  /** 创建时间 */
  @Column(name = "crt_time")
  @ApiModelProperty(hidden = true)
  private Date crtTime;

  @Column(name = "upd_id")
  @ApiModelProperty(hidden = true)
  private Integer updId;

  /** 修改人名称 */
  @Column(name = "upd_name")
  @ApiModelProperty(hidden = true)
  private String updName;

  /** 修改时间 */
  @Column(name = "upd_time")
  @ApiModelProperty(hidden = true)
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
   * 获取部门名
   *
   * @return name - 部门名
   */
  public String getName() {
    return name;
  }

  /**
   * 设置部门名
   *
   * @param name 部门名
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * 获取部门类型(0-门诊部门；1-公司部门）
   *
   * @return type - 部门类型(0-门诊部门；1-公司部门）
   */
  public Byte getType() {
    return type;
  }

  /**
   * 设置部门类型(0-门诊部门；1-公司部门）
   *
   * @param type 部门类型(0-门诊部门；1-公司部门）
   */
  public void setType(Byte type) {
    this.type = type;
  }

  /**
   * 获取自定义部门排序
   *
   * @return int
   */
  public Integer getOrderNum() {
    return orderNum;
  }

  /**
   * 设置自定义部门排序
   *
   * @param orderNum 参数
   * @return int
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
