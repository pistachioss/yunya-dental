package com.yunya.feign.system.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 用户信息
 *
 * @author wanghaobin
 * @create 2017-06-21 8:12
 */
@Data
@ToString
public class UserInfo implements Serializable {
  /** 用户ID */
  private String id;
  /** 用户名 */
  private String username;
  /** 密码 */
  private String password;
  /** 用户姓名 */
  private String name;
  /** 出生日期 */
  private String birthday;
  /** 手机号 */
  private String mobilePhone;
  /** 性别(0-男；1-女) */
  private Byte gender;
  /** 描述 */
  private String description;
  /** -------------用户员工信息--------------- */
  /** 员工ID */
  private String employeeId;
  /** 身份证号 */
  private String identity;
  /** 工作状态(0-试用；1-正式；2-实习；3-离职) */
  private Byte workStatus;
}
