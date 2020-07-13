package com.yunya.framework.auth.jwt;

import java.io.Serializable;
import java.util.Objects;

/**
 * IJWTInfo接口实现类
 *
 * @author ace
 * @date 2017/9/10
 */
public class JWTInfo implements Serializable, IJWTInfo {

  private String username;
  private String userId;
  private String name;

  public JWTInfo(String username, String userId, String name) {
    this.username = username;
    this.userId = userId;
    this.name = name;
  }

  @Override
  public String getUniqueName() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  @Override
  public String getId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  @Override
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    JWTInfo jwtInfo = (JWTInfo) o;

    if (!Objects.equals(username, jwtInfo.username)) {
      return false;
    }
    return Objects.equals(userId, jwtInfo.userId);
  }

  @Override
  public int hashCode() {
    int result = username != null ? username.hashCode() : 0;
    result = 31 * result + (userId != null ? userId.hashCode() : 0);
    return result;
  }
}
