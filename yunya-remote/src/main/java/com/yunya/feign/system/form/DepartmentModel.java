package com.yunya.feign.system.form;

import com.yunya.framework.common.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介: 部门模版信息查询模型
 *
 * @author: chow
 * @date: 2020/7/15 11:27
 * @description:
 * @since: 1.0.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString
public class DepartmentModel extends BaseEntity implements Serializable {

  private Integer id;

  /** 部门名 */
  private String name;

  /** 部门类型(0-门诊部门；1-公司部门） */
  private Byte type;

  /** 自定义排序 */
  private Integer orderNum;
}
