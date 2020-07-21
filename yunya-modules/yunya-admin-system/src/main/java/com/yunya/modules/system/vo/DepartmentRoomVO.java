package com.yunya.modules.system.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介: 科室VO模型
 *
 * @author: chow
 * @date: 2020/7/20 19:39
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
public class DepartmentRoomVO implements Serializable {
  /** 科室ID */
  private Integer id;
  /** 科室名称 */
  private String name;
  /** 启用状态 */
  private Boolean inservice;
}
