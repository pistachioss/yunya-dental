package com.yunya.modules.system.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简单介绍:</br> 组织部门信息vo
 *
 * @author: chow
 * @date: 2020/6/5 11:42
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
public class OrgDeptVO implements Serializable {
  /** 组织部门ID */
  private Integer id;
  /** 组织部门父ID */
  private Integer parentId;
  /** 组织ID */
  private Integer companyId;
  /**组织名称*/
  private String companyName;
  /** 部门模版ID */
  private Integer deptId;
  /** 部门名称 */
  private String deptName;
  /** 部门类型 */
  private Byte type;
  /**自定义排序*/
  private Integer orderNum;
}
