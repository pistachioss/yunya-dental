package com.yunya.feign.tariff.domain.model;

import com.yunya.framework.common.annation.Excel;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介:
 *
 * @author: chow
 * @date: 2020/8/5 17:15
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
public class BaseTariffImportModel implements Serializable {

  /** 价目表编码 */
  @Excel(name = "价目表编码", type = Excel.Type.IMPORT)
  private String itemNumber;

  /** 项目名称 */
  @Excel(name = "项目名称", type = Excel.Type.IMPORT)
  private String name;

  /** 项目分类编号 */
  @Excel(name = "项目分类编号", type = Excel.Type.IMPORT)
  private String tariffCategoryNumber;

  /** 项目分类名称 */
  @Excel(name = "项目分类名称", type = Excel.Type.IMPORT)
  private String tariffCategoryName;

  /** 英文名称 */
  @Excel(name = "项目英文名称", type = Excel.Type.IMPORT)
  private String englishName;

  /** 单位 */
  @Excel(name = "单位", type = Excel.Type.IMPORT)
  private String unit;

  /** 价格 */
  @Excel(name = "价格", type = Excel.Type.IMPORT)
  private BigDecimal price;
}
