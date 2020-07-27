package com.yunya.modules.system.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介: 入账方式分类VO模型
 *
 * @author: chow
 * @date: 2020/7/24 19:21
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
public class AccountTypeVO implements Serializable {
  private Integer id;
  /** 入账方式 */
  private String name;
  /** 是否系统默认（系统默认无法修改/删除） */
  private Boolean sysDefault;
  /** 是否启用 */
  private Boolean inservice;
}
