package com.yunya.feign.tariff.domain.model;

import com.yunya.framework.common.annation.Excel;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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
  @NotBlank(message = "商品项目编码不能为空！")
  private String itemNumber;

  /** 商品项目名称 */
  @Excel(name = "商品项目名称", type = Excel.Type.IMPORT)
  @NotBlank(message = "商品项目名称不能为空！")
  private String name;

  /** 商品分类编号 */
  @Excel(name = "商品分类编号", type = Excel.Type.IMPORT)
  @NotBlank(message = "商品分类编号不能为空！")
  private String oralTariffCategoryNumber;

  /** 商品分类名称 */
  @Excel(name = "商品分类名称", type = Excel.Type.IMPORT)
  @NotBlank(message = "商品分类名称不能为空！")
  private String oralTariffCategoryName;

  /** 商品项目英文名称 */
  @Excel(name = "商品英文名称", type = Excel.Type.IMPORT)
  private String englishName;

  /** 单位 */
  @Excel(name = "单位", type = Excel.Type.IMPORT)
  @NotBlank(message = "单位不能为空！")
  private String unit;

  /** 价格 */
  @Excel(name = "单价", type = Excel.Type.IMPORT, cellType = Excel.ColumnType.NUMERIC)
  @NotNull(message = "单价不能为空！")
  private BigDecimal price;
}
