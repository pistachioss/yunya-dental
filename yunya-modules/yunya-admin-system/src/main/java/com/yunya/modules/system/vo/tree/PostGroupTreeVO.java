package com.yunya.modules.system.vo.tree;

import com.yunya.framework.common.model.TreeNode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 简单介绍:</br> 岗位组树VO
 *
 * @author: chow
 * @date: 2020/6/10 11:49
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
public class PostGroupTreeVO extends TreeNode {
  /** 岗位组名称 */
  private String name;
  /** 自定义排序 */
  private Integer orderNum;
  /**允许操作（编辑/删除）*/
  private Boolean allowOperation;
}
