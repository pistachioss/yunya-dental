package com.yunya.modules.system.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介: 个人页面字段信息VO
 *
 * @author: chow
 * @date: 2020/9/1 10:57
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
public class PersonalPageFieldVO implements Serializable {

  /** 字段ID */
  private Integer id;
  /** 字段名称 */
  private String fieldName;
  /** 是否隐藏 */
  private Boolean hide;
  /** 字段排序 */
  private Integer orderNum;
  /** 是否默认 */
  private Boolean defaultValue;
  /** 是否启用 */
  private Boolean inservice;
}
