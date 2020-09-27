package com.yunya.feign.system.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * 简介: 前端用户信息VO
 *
 * @author: chow
 * @date: 2020/9/23 13:18
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("前端用户信息VO")
public class FrontUserInfoVO {
  /** 用户ID */
  @ApiModelProperty("用户ID")
  private String id;
  /** 当前登陆组织ID */
  @ApiModelProperty("当前登陆组织ID")
  private Integer currentOrgId;
  /** 用户名 */
  @ApiModelProperty("username")
  private String username;
  /** 用户姓名 */
  @ApiModelProperty("用户姓名")
  private String name;
  /** 出生日期 */
  @ApiModelProperty("出生日期")
  private String birthday;
  /** 手机号 */
  @ApiModelProperty("手机号")
  private String mobilePhone;
  /** 性别(0-男；1-女) */
  @ApiModelProperty("性别(0-男；1-女)")
  private Byte gender;
  /** 描述 */
  @ApiModelProperty("用户描述")
  private String description;
  /** -------------用户员工信息--------------- */
  /** 员工ID */
  @ApiModelProperty("员工ID")
  private String employeeId;
  /** 身份证号 */
  @ApiModelProperty("身份证号")
  private String identity;
  /** 工作状态(0-试用；1-正式；2-实习；3-离职) */
  @ApiModelProperty("工作状态-试用: 0, 正式: 1， 离职:2；实习: 3；")
  private Byte workStatus;
}
