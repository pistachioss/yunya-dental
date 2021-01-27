package com.yunya.feign.system.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介:
 *
 * @author: chow
 * @date: 2020/9/27 14:07
 * @description:
 * @since: 1.0.0
 */
@ApiModel("员工信息VO")
@Data
@ToString
public class EmployeeInfoVO implements Serializable {
  /** 用户ID */
  @ApiModelProperty("用户ID")
  private Integer userId;
  /** 员工ID */
  @ApiModelProperty("员工ID")
  private Integer employeeId;
  /** 员工姓名 */
  @ApiModelProperty("员工姓名")
  private String name;
  /** 性别 */
  @ApiModelProperty("性别 0- 男；1-女")
  private Byte gender;
  /** 手机号 */
  @ApiModelProperty("手机号")
  private String mobilePhone;
  /** 组织ID */
  @ApiModelProperty("组织ID")
  private Integer orgId;
  /** 组织名称 */
  @ApiModelProperty("组织名称")
  private String orgName;
  /** 岗位ID */
  @ApiModelProperty("岗位组ID")
  private Integer postGroupId;
  /** 岗位名称 */
  @ApiModelProperty("岗位组名称")
  private String postGroupName;
  /** 岗位组ID */
  @ApiModelProperty("岗位ID")
  private Integer postId;
  /** 岗位组名称 */
  @ApiModelProperty("岗位名称")
  private String postName;
  /** 在职状态 */
  @ApiModelProperty("在职状态:0-试用期；1-已转正；2-离职；-实习")
  private Byte workStatus;
}
