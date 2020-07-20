package com.yunya.feign.system.form;

import com.yunya.framework.common.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介: 岗位组查询模型
 *
 * @author: chow
 * @date: 2020/7/17 17:38
 * @description:
 * @since: 1.0.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString
public class PostGroupModel extends BaseEntity implements Serializable {
  private Integer id;

  /** 上级岗位分类ID */
  private Integer parentId;

  /** 岗位分类名称 */
  private String name;

  /** 自定义排序 */
  private Integer orderNum;

  /** 允许操作（编辑/删除） */
  private Boolean allowOperation;
}
