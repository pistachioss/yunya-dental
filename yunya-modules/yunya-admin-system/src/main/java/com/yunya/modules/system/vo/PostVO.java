package com.yunya.modules.system.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简单介绍:</br> 岗位信息VO
 *
 * @author: chow
 * @date: 2020/6/3 13:53
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
public class PostVO implements Serializable {
  /** 岗位ID */
  private Integer id;
  /** 岗位名称 */
  private String name;
  /** 岗位组ID */
  private Integer postGroupId;
  /** 岗位组名称 */
  private String postGroupName;
  /** 允许操作（编辑/删除） */
  private Boolean allowOperation;
  /** 是否启用 */
  private Boolean inservice;
}
