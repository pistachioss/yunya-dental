package com.yunya.modules.system.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简单介绍:</br> 岗位组VO对象
 *
 * @author: chow
 * @date: 2020/6/10 10:59
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
public class PostGroupVO implements Serializable {
  /** 岗位组ID */
  private Integer id;
  /** 岗位组父ID */
  private Integer parentId;
  /** 岗位组名称 */
  private String name;
  /** 自定义排序 */
  private Integer orderNum;
  /** 允许被操纵(编辑/删除) */
  private Boolean allowOperation;
  /** 是否启用 */
  private Boolean inservice;
}
