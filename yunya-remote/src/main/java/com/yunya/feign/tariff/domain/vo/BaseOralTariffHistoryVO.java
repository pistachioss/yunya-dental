package com.yunya.feign.tariff.domain.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介: 商品项目变更记录VO
 *
 * @author: chow
 * @date: 2020/8/3 20:18
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
public class BaseOralTariffHistoryVO implements Serializable {
  private Integer id;

  /** 商品价目表ID */
  private Integer oralTariffId;

  /** 商品分类ID */
  private Integer oralTariffCategoryId;

  /** 商品分类名称 */
  private Integer oralTariffCategoryName;

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
