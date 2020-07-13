package com.yunya.modules.system.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@ApiModel("地区参数封装模型")
@Table(name = "sys_districts")
public class SysDistricts {
  /** 编号 */
  @Id
  @ApiModelProperty(value = "编号", required = true)
  private Integer id;

  /** 上级编号 */
  @ApiModelProperty(value = "上级编号", required = true)
  private Integer parentId;

  /** 层级 */
  @ApiModelProperty(value = "层级（0-省；1-市；2-区）", required = true)
  private Boolean deep;

  /** 名称 */
  @ApiModelProperty(value = "地区名称",required = true)
  private String name;

  /** 拼音 */
  @ApiModelProperty(value = "拼音",required = true)
  private String pinyin;

  /** 拼音缩写 */
  @ApiModelProperty(value = "拼音缩写", required = true)
  @Column(name = "pinyin_short")
  private String pinyinShort;

  /** 扩展名 */
  @ApiModelProperty(value = "扩展名", required = true)
  @Column(name = "ext_name")
  private String extName;

  /** 是否启用 */
  private Boolean inservice;

  /** 创建人id */
  @Column(name = "crt_id")
  private String crtId;

  /** 创建时间 */
  @Column(name = "crt_time")
  private Date crtTime;

  /** 更新人id */
  @Column(name = "upd_id")
  private String updId;

  /** 最后更新时间 */
  @Column(name = "upd_time")
  private Date updTime;

  /**
   * 获取编号
   *
   * @return id - 编号
   */
  public Integer getId() {
    return id;
  }

  /**
   * 设置编号
   *
   * @param id 编号
   */
  public void setId(Integer id) {
    this.id = id;
  }

  /**
   * 获取上级编号
   *
   * @return parentId - 上级编号
   */
  public Integer getParentId() {
    return parentId;
  }

  /**
   * 设置上级编号
   *
   * @param parentId 上级编号
   */
  public void setParentId(Integer parentId) {
    this.parentId = parentId;
  }

  /**
   * 获取层级
   *
   * @return deep - 层级
   */
  public Boolean getDeep() {
    return deep;
  }

  /**
   * 设置层级
   *
   * @param deep 层级
   */
  public void setDeep(Boolean deep) {
    this.deep = deep;
  }

  /**
   * 获取名称
   *
   * @return name - 名称
   */
  public String getName() {
    return name;
  }

  /**
   * 设置名称
   *
   * @param name 名称
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * 获取拼音
   *
   * @return pinyin - 拼音
   */
  public String getPinyin() {
    return pinyin;
  }

  /**
   * 设置拼音
   *
   * @param pinyin 拼音
   */
  public void setPinyin(String pinyin) {
    this.pinyin = pinyin;
  }

  /**
   * 获取拼音缩写
   *
   * @return pinyin_short - 拼音缩写
   */
  public String getPinyinShort() {
    return pinyinShort;
  }

  /**
   * 设置拼音缩写
   *
   * @param pinyinShort 拼音缩写
   */
  public void setPinyinShort(String pinyinShort) {
    this.pinyinShort = pinyinShort;
  }

  /**
   * 获取扩展名
   *
   * @return ext_name - 扩展名
   */
  public String getExtName() {
    return extName;
  }

  /**
   * 设置扩展名
   *
   * @param extName 扩展名
   */
  public void setExtName(String extName) {
    this.extName = extName;
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
   * 获取创建人id
   *
   * @return crt_id - 创建人id
   */
  public String getCrtId() {
    return crtId;
  }

  /**
   * 设置创建人id
   *
   * @param crtId 创建人id
   */
  public void setCrtId(String crtId) {
    this.crtId = crtId;
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
   * 获取更新人id
   *
   * @return upd_id - 更新人id
   */
  public String getUpdId() {
    return updId;
  }

  /**
   * 设置更新人id
   *
   * @param updId 更新人id
   */
  public void setUpdId(String updId) {
    this.updId = updId;
  }

  /**
   * 获取最后更新时间
   *
   * @return upd_time - 最后更新时间
   */
  public Date getUpdTime() {
    return updTime;
  }

  /**
   * 设置最后更新时间
   *
   * @param updTime 最后更新时间
   */
  public void setUpdTime(Date updTime) {
    this.updTime = updTime;
  }
}
