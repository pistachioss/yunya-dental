package com.yunya.feign.system.form;

import com.yunya.framework.common.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介:
 *
 * @author: chow
 * @date: 2020/7/16 17:39
 * @description:
 * @since: 1.0.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString
public class PostModel extends BaseEntity implements Serializable {
  private Integer id;

  /** 岗位名称 */
  private String name;

  /** 岗位组ID */
  private Integer postGroupId;

  /** 自定义排序 */
  private Integer orderNum;

  /** 允许操纵（编辑/删除） */
  private Boolean allowOperation;
}
