package com.yunya.feign.tariff.domain.model;

import com.yunya.framework.common.annation.Excel;
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
  @Excel(name = "商品项目编码", type = Excel.Type.IMPORT)
  private String itemNumber;

  /** 商品项目名称 */
  @Excel(name = "商品项目名称", type = Excel.Type.IMPORT)
  private String name;

  /** 商品分类编号 */
  @Excel(name = "商品分类编号", type = Excel.Type.IMPORT)
  private String oralTariffCategoryNumber;

  /** 商品分类名称 */
  @Excel(name = "商品分类名称", type = Excel.Type.IMPORT)
  private String oralTariffCategoryName;

  /** 商品项目英文名称 */
  @Excel(name = "商品英文名称", type = Excel.Type.IMPORT)
  private String englishName;

  /** 单位 */
  @Excel(name = "单位", type = Excel.Type.IMPORT)
  private String unit;

  /** 价格 */
  @Excel(name = "单价", type = Excel.Type.IMPORT, cellType = Excel.ColumnType.NUMERIC)
  private BigDecimal price;
}
