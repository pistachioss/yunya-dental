package com.yunya.auth.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 用户认证参数封装模型
 *
 * @author chow
 */
@ApiModel("用户登陆信息")
public class JwtRequestFrom implements Serializable {

  private static final long serialVersionUID = -8445943548965154778L;
  /** 用户名 */
  @ApiModelProperty(value = "用户名", required = true)
  @NotBlank(message = "用户名不能为空！")
  private String username;
  /** 密码 */
  @ApiModelProperty(value = "密码", required = true)
  @NotBlank(message = "密码不能为空！")
  private String password;

  public JwtRequestFrom() {}

  public JwtRequestFrom(String username, String password) {
    this.username = username;
    this.password = password;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }
}
