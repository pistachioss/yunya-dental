package com.yunya.feign.treatment.domain.vo;

import com.yunya.framework.common.annation.Excel;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 描述: 商品项目VO
 *
 * @author GaoLuding
 * @create 2020-05-26 17:18
 */
@Data
@ToString
public class BaseOralTariffVO implements Serializable {
  /** 商品项目ID */
  private Integer id;

  /** 商品分类id */
  private Integer oralTariffCategoryId;

  /** 商品分类名称 */
  @Excel(name = "商品分类名称")
  private String oralTariffCategoryName;

  /** 商品分类编号 */
  @Excel(name = "商品分类编号")
  private String oralTariffCategoryNumber;

  /** 项目编码 */
  @Excel(name = "商品项目编码")
  private String itemNumber;

  /** 项目名称 */
  @Excel(name = "商品项目名称")
  private String name;

  /** 拼音 */
  private String pinyin;

  /** 项目英文名称 */
  @Excel(name = "商品英文名称")
  private String englishName;

  /** 单位 */
  @Excel(name = "单位")
  private String unit;

  /** 价格 */
  @Excel(name = "单价")
  private BigDecimal price;

  /** 是否启用 */
  private Boolean inservice;
}
