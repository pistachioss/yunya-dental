package com.yunya.modules.system.vo;

import com.yunya.framework.common.model.TreeNode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简单介绍:</br> 组织部门树VO
 *
 * @author: chow
 * @date: 2020/6/5 14:09
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
public class OrgDeptTreeVO extends TreeNode implements Serializable {
  /** 组织ID */
  private Integer companyId;
  /** 组织名称 */
  private String companyName;
  /** 部门模版ID */
  private Integer deptId;
  /** 部门模版名称 */
  private String deptName;
  /** 部门类型 */
  private Byte type;
  /** 自定义排序 */
  private Integer orderNum;
}
