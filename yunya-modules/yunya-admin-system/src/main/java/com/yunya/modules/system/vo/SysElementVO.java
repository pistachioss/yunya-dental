package com.yunya.modules.system.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简单介绍:</br>
 *
 * @author: chow
 * @date: 2020/6/24 16:55
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
public class SysElementVO implements Serializable {
  private String id;
  /** 资源权限编码 */
  private String code;
  /** 功能资源类型（uri;button）*/
  private String type;
  /** 资源名称 */
  private String name;
  /** 资源请求路径 */
  private String uri;
  /** 资源关联菜单 */
  private Integer menuId;
  /** 上级ID */
  private Integer parentId;
  /** 上级按钮名称 */
  private String parentName;
  /** 资源树状检索路径 */
  private String path;
  /** 资源请求类型 */
  private String method;
  /** 描述 */
  private String description;
  /** 是否启用 */
  private Boolean inservice;
}
