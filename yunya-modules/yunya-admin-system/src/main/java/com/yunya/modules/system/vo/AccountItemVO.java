package com.yunya.modules.system.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介:
 *
 * @author: chow
 * @date: 2020/7/27 09:53
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
public class AccountItemVO implements Serializable {
  private Integer id;
  /** 入账分类id */
  private Integer accountTypeId;
  /** 入账方式分类名称 */
  private String accountTypeName;
  /** 入账方式名称 */
  private String name;
  /** 类型,0.现金,1.预售,2.优惠,3平台结算 */
  private Byte type;
  /** 是否启用 */
  private Boolean inservice;
}
