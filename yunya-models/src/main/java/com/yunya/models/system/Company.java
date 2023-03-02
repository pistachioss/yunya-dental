package com.yunya.models.system;

import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Table(name = "company")
public class Company {
  @Id
  @ApiModelProperty(hidden = true)
  @GeneratedValue(generator = "JDBC")
  private Integer id;

  /** 父节点编号 */
  @Column(name = "parent_id")
  @ApiModelProperty("组织父ID")
  private Integer parentId;

  /** 公司名称 */
  @NotBlank(message = "组织名称为空！")
  @Size(max = 50, message = "组织名称长度不能超过50个字符")
  @ApiModelProperty("组织名称")
  private String name;

  /** 公司属性0:公司,1:区域管理,2:医疗机构,3:其他 */
  @NotNull(message = "组织类型为空！")
  @ApiModelProperty(value = "组织类型（0:公司,1:区域管理,2:医疗机构,3:其他）", required = true)
  private Byte type;

  /** 组织统一社会代码 */
  @ApiModelProperty("组织统一信用代码")
  @Column(name = "credit_code")
  @Size(max = 18, message = "统一信用代码长度不能超过18个字符")
  private String creditCode;

  /** 自定义排序 */
  @NotNull(message = "自定义组织排序为空！")
  @Column(name = "order_num")
  @ApiModelProperty(value = "自定义排序", required = true)
  private Integer orderNum;

  @Column(name = "enable_qzt_sync")
  private Boolean enableQztSync;

  @Column(name = "qzt_institution_code")
  private String qztInstitutionCode;

  @Column(name = "enable_bj_sync")
  private Boolean enableBjSync;

  @Column(name = "bj_institution_code")
  private String bjInstitutionCode;

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

  /** 更新人ID */
  @Column(name = "upd_id")
  @ApiModelProperty(hidden = true)
  private Integer updId;

  /** 修改时间 */
  @Column(name = "upd_time")
  @ApiModelProperty(hidden = true)
  private Date updTime;

  /** 修改人名称 */
  @Column(name = "upd_name")
  @ApiModelProperty(hidden = true)
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
   * 获取父节点编号
   *
   * @return parent_id - 父节点编号
   */
  public Integer getParentId() {
    return parentId;
  }

  /**
   * 设置父节点编号
   *
   * @param parentId 父节点编号
   */
  public void setParentId(Integer parentId) {
    this.parentId = parentId;
  }

  /**
   * 获取公司名称
   *
   * @return name - 公司名称
   */
  public String getName() {
    return name;
  }

  /**
   * 设置公司名称
   *
   * @param name 公司名称
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * 获取公司属性0:公司,1:区域管理,2:医疗机构,3:其他
   *
   * @return type - 公司属性0:公司,1:区域管理,2:医疗机构,3:其他
   */
  public Byte getType() {
    return type;
  }

  /**
   * 设置公司属性0:公司,1:区域管理,2:医疗机构,3:其他
   *
   * @param type 公司属性0:公司,1:区域管理,2:医疗机构,3:其他
   */
  public void setType(Byte type) {
    this.type = type;
  }

  /**
   * 获取组织统一社会代码
   *
   * @return creditCode
   */
  public String getCreditCode() {
    return creditCode;
  }

  /**
   * 设置组织统一社会代码
   *
   * @param creditCode 组织统一社会代码
   */
  public void setCreditCode(String creditCode) {
    this.creditCode = creditCode;
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

  /**
   * 获取更新人ID
   *
   * @return upd_id - 更新人ID
   */
  public Integer getUpdId() {
    return updId;
  }

  /**
   * 设置更新人ID
   *
   * @param updId 更新人ID
   */
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
}
