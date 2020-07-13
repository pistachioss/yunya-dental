package com.yunya.framework.common.utils;

import com.yunya.framework.common.model.TreeNode;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 树列表构建工具类
 *
 * @author Ace
 * @date 2017/6/12
 */
public class TreeUtil {
  /**
   * 两层循环实现建树
   *
   * @param treeNodes 传入的树节点列表
   * @return
   */
  public static <T extends TreeNode> List<T> build(List<T> treeNodes, Integer root) {

    List<T> trees = new ArrayList<T>();

    treeNodes.forEach(
        treeNode -> {
          if (root.equals(treeNode.getParentId())) {
            trees.add(treeNode);
          }
          treeNodes.stream()
              .filter(it -> it.getParentId().equals(treeNode.getId()))
              .forEach(
                  it -> {
                    if (treeNode.getChildren() == null) {
                      treeNode.setChildren(new ArrayList<>());
                    }
                    treeNode.add(it);
                  });
        });
    return trees;
  }

  /**
   * 使用递归方法建树
   *
   * @param treeNodes 节点
   * @return
   */
  public static <T extends TreeNode> List<T> buildByRecursive(List<T> treeNodes, Object root) {

    return treeNodes.stream()
        .filter(treeNode -> root.equals(treeNode.getParentId()))
        .map(treeNode -> findChildren(treeNode, treeNodes))
        .collect(Collectors.toList());
  }

  /**
   * 递归查找子节点
   *
   * @param treeNodes 树节点
   * @return
   */
  public static <T extends TreeNode> T findChildren(T treeNode, List<T> treeNodes) {
    treeNodes.stream()
        .filter(it -> treeNode.getId().equals(it.getParentId()))
        .forEach(
            it -> {
              if (treeNode.getChildren() == null) {
                treeNode.setChildren(new ArrayList<>());
              }
              treeNode.add(findChildren(it, treeNodes));
            });
    return treeNode;
  }
}
