package com.yunya.modules.system.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简单介绍: 用户可登陆组织信息返回（员工管理用）
 *
 * @author: chow
 * @date: 2020/7/1 09:55
 * @description:
 * @since: 1.0.0
 */
@ApiModel("用户可登陆组织信息返回（员工管理用）")
@Data
@ToString
public class SysUserPostOrgVO implements Serializable {
  /** 可登录组织ID */
  @ApiModelProperty("可登录组织主键ID")
  private Integer id;
  /** 组织ID */
  @ApiModelProperty("组织ID")
  private Integer orgId;
  /** 组织名称 */
  @ApiModelProperty("组织名称")
  private String name;
  /** 组织简称 */
  @ApiModelProperty("组织简称")
  private String abbreviation;
  /** 组织类型 */
  @ApiModelProperty("组织属性0:公司,1:区域管理,2:医疗机构,3:其他")
  private Byte type;
  /** 组织部门ID */
  @ApiModelProperty("组织部门ID")
  private Integer orgDeptId;
  /** 部门名称 */
  @ApiModelProperty("部门名称")
  private String deptName;
  /** 岗位ID */
  @ApiModelProperty("岗位ID")
  private Integer postId;
  /** 岗位组ID */
  @ApiModelProperty("岗位组ID")
  private Integer postGroupId;
}
