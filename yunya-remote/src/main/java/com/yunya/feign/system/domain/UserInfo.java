package com.yunya.feign.system.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户信息
 *
 * @author wanghaobin
 * @create 2017-06-21 8:12
 */
public class UserInfo implements Serializable {
  /** 用户ID */
  public String id;
  /** 用户名 */
  public String username;
  /** 密码 */
  public String password;
  /** 用户姓名 */
  public String name;
  /** 更新时间 */
  private Date updTime;
  /** 描述 */
  private String description;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Date getUpdTime() {
    return updTime;
  }

  public void setUpdTime(Date updTime) {
    this.updTime = updTime;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }
}
