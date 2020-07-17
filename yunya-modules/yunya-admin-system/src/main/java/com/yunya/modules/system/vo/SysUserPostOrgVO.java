package com.yunya.modules.system.vo;

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
@Data
@ToString
public class SysUserPostOrgVO implements Serializable {
  /**可登录组织ID*/
  private Integer id;
  /** 组织ID */
  private Integer orgId;
  /** 组织名称 */
  private String name;
  /** 组织简称 */
  private String abbreviation;
  /** 组织类型 */
  private Byte type;
  /** 组织部门ID */
  private Integer orgDeptId;
  /** 岗位ID */
  private Integer postId;
}
