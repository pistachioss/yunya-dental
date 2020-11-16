package com.yunya.feign.treatment.domain.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 描述: 基础价目表VO
 *
 * @author GaoLuding
 * @create 2020-05-26 14:07
 */
@Data
@ToString
public class BaseTariffVO implements Serializable {
  /** 基础项目ID */
  private Integer id;

  /** 基础分类id */
  private Integer tariffCategoryId;

  /** 基础分类名称 */
  private String tariffCategoryName;

  /** 基础分类编号 */
  private String tariffCategoryNumber;

  /** 项目编码 */
  private String itemNumber;

  /** 项目名称 */
  private String name;

  /** 拼音 */
  private String pinyin;

  /** 项目英文名称 */
  private String englishName;

  /** 单位 */
  private String unit;

  /** 价格 */
  private BigDecimal price;

  /** 是否启用 */
  private Boolean inservice;
}
