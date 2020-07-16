package com.yunya.feign.system.form;

import com.yunya.framework.common.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介: 组织部门参数模型
 *
 * @author: chow
 * @date: 2020/7/16 17:07
 * @description:
 * @since: 1.0.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString
public class OrgDepartmentModel extends BaseEntity implements Serializable {

  private Integer id;

  /** 公司父部门ID */
  private Integer parentId;

  /** 公司ID */
  private Integer companyId;

  /** 部门模版ID */
  private Integer departmentId;
}
