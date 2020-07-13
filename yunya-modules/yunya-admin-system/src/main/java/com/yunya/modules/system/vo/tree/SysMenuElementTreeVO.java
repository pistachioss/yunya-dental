package com.yunya.modules.system.vo.tree;

import com.yunya.framework.common.model.TreeNode;
import com.yunya.modules.system.vo.SysElementVO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 简单介绍:</br> 菜单功能树对象
 *
 * @author: chow
 * @date: 2020/6/30 10:48
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
public class SysMenuElementTreeVO extends TreeNode implements Serializable {
  /** 系统id */
  private Integer systemId;
  /** 菜单类型 0-web;1-app */
  private Byte menuType;
  /** 路径编码 */
  private String code;
  /** 标题 */
  private String title;
  /** 页面资源路径 */
  private String href;
  /** 菜单图标 */
  private String icon;
  /** 菜单类型 dirt-目录；menu-菜单 */
  private String type;
  /** 自定义排序 */
  private Integer orderNum;
  /** 菜单描述 */
  private String description;
  /** 菜单上下级关系 */
  private String path;
  /** 启用禁用 */
  private Boolean inservice;
  /** 页面功能 */
  private List<SysElementVO> elements;
}
