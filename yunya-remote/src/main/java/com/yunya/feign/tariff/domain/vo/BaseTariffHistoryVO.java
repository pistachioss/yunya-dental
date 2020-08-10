package com.yunya.feign.tariff.domain.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介: 价目表变更记录VO
 *
 * @author: chow
 * @date: 2020/8/5 15:42
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
public class BaseTariffHistoryVO implements Serializable {
  private Integer id;

  /** 基础价目表ID */
  private Integer tariffId;

  /** 价目表分类ID */
  private Integer tariffCategoryId;

  /** 价目表分类名称 */
  private String tariffCategoryName;

  /** 项目编码 */
  private String itemNumber;

  /** 项目名称 */
  private String name;

  /** 创建人ID */
  private Integer crtId;

  /** 创建人名称 */
  private String crtName;

  /** 创建时间 */
  private String crtTime;
}
