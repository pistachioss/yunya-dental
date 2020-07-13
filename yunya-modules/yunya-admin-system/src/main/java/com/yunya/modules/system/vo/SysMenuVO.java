package com.yunya.modules.system.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简单介绍:</br> 系统菜单VO
 *
 * @author: chow
 * @date: 2020/6/24 10:49
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
public class SysMenuVO implements Serializable {
  private Integer id;
  /** 系统id */
  private Integer systemId;
  /** 路径编码 */
  private String code;
  /** 标题 */
  private String title;
  /** 父级节点 */
  private Integer parentId;
  /** 资源路径 */
  private String href;
  /** 图标 */
  private String icon;
  /** 菜单类型 */
  private String type;
  /** 排序 */
  private Integer orderNum;
  /** 描述 */
  private String description;
  /** 菜单上下级关系 */
  private String path;
  /** 启用禁用 */
  private Boolean inservice;
}
