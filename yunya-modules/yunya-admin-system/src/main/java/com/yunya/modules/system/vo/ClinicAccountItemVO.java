package com.yunya.modules.system.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介: 门诊入账方式VO
 *
 * @author: chow
 * @date: 2020/7/27 11:24
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
public class ClinicAccountItemVO implements Serializable {
  private Integer id;

  /** 公司端对应的诊所ID */
  private Integer orgId;

  /** 组织名称 */
  private String orgName;

  /** 公司端对应支付方式分类表的ID */
  private Integer accountItemId;

  /** 入账方式名称 */
  private String accountItemName;

  /** 入账方式类型 */
  private Byte type;

  /** 是否启用 */
  private Boolean inservice;
}
