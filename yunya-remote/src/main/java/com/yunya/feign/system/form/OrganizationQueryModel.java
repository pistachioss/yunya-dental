package com.yunya.feign.system.form;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介: 组织信息列表查询模型
 *
 * @author: chow
 * @date: 2020/7/16 09:21
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
public class OrganizationQueryModel implements Serializable {
  private Integer id;
  /** 组织名称 */
  private String name;
  /** 组织类型 */
  private Byte[] types;
  /** 组织编号 */
  private String clinicNumber;
  /** 组织简称 */
  private String abbreviation;
}
