package com.yunya.framework.common.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 树节点参数封装
 *
 * @author Ace
 * @date 2017/6/12
 */
public class TreeNode implements Serializable {
  /** 节点ID */
  protected Integer id;
  /** 节点名称 */
  protected String name;
  /** 节点父ID */
  protected Integer parentId;

  public List<TreeNode> getChildren() {
    return children;
  }

  public void setChildren(List<TreeNode> children) {
    this.children = children;
  }

  List<TreeNode> children = new ArrayList<TreeNode>();

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Integer getParentId() {
    return parentId;
  }

  public void setParentId(Integer parentId) {
    this.parentId = parentId;
  }

  public void add(com.yunya.framework.common.model.TreeNode node) {
    children.add(node);
  }
}
