package com.yunya.feign.tariff.domain.model;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介: 商品项目导入模型
 *
 * @author: chow
 * @date: 2020/8/4 13:29
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
public class BaseOralTariffImportModel implements Serializable {

  /** 商品项目编码 */
  private String itemNumber;

  /** 商品项目名称 */
  private String name;

  /** 商品分类编号 */
  private String oralTariffCategoryNumber;

  /** 商品分类名称 */
  private String oralTariffCategoryName;

  /** 商品项目英文名称 */
  private String englishName;

  /** 单位 */
  private String unit;

  /** 价格 */
  private BigDecimal price;
}
