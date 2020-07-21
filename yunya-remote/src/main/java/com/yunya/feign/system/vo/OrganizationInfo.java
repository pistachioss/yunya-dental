package com.yunya.feign.system.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介: 组织（公司）全部信息
 *
 * @author: chow
 * @date: 2020/7/15 13:28
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
public class OrganizationInfo implements Serializable {
  private Integer id;
  /** 公司名称 */
  private String name;
  /** 公司属性0:公司,1:区域管理,2:医疗机构,3:其他 */
  private String type;
  /** 组织统一社会代码 */
  private String creditCode;
  /** 门诊编号 */
  private String clinicNumber;
  /** 诊所简称 */
  private String abbreviation;
}
