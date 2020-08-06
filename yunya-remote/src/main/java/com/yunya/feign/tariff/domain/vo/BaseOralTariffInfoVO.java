package com.yunya.feign.tariff.domain.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 简介: 基础商品项目信息VO
 *
 * @author: chow
 * @date: 2020/8/3 16:57
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
public class BaseOralTariffInfoVO implements Serializable {
  /** 基础商品项目ID */
  private Integer id;

  /** 商品分类ID */
  private Integer oralTariffCategoryId;

  /** 商品分类名称 */
  private String oralTariffCategoryName;

  /** 商品分类编号 */
  private String oralTariffCategoryNumber;

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

  /** 门诊项目价格信息列表 */
  private List<ClinicItemPriceVO> clinicItemInfos;
}
