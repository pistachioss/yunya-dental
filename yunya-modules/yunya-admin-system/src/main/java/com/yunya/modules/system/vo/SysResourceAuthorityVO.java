package com.yunya.modules.system.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简单介绍: 资源权限VO类
 *
 * @author: chow
 * @date: 2020/6/30 19:05
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
public class SysResourceAuthorityVO implements Serializable {
  /**岗位资源权限ID*/
  private Integer id;
  /** 岗位ID */
  private Integer postId;
  /** 岗位名称 */
  private String postName;
  /** 岗位组ID */
  private Integer postGroupId;
  /** 岗位组名称 */
  private String postGroupName;
  /** 资源ID */
  private Integer resourceId;
  /** 资源类型 */
  private Byte resourceType;
}
