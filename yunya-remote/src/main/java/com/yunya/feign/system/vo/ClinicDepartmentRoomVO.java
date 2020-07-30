package com.yunya.feign.system.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介: 门诊科室VO
 *
 * @author: chow
 * @date: 2020/7/21 09:27
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
public class ClinicDepartmentRoomVO implements Serializable {
  /** 门诊科室ID */
  private Integer clinicDeptRoomId;
  /** 科室ID */
  private Integer deptRoomId;
  /** 科室名称 */
  private String deptRoomName;
  /** 组织（诊所）ID */
  private Integer orgId;
  /** 组织(诊所)简称 */
  private String abbreviation;
  /** 启用状态 */
  private Boolean inservice;
}
