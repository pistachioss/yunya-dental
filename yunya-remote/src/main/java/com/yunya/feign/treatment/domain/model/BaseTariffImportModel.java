package com.yunya.feign.treatment.domain.model;

import com.yunya.framework.common.annation.Excel;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介: 基础价目表导入模型
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
  @Excel(name = "项目编号", type = Excel.Type.IMPORT)
  @NotBlank(message = "价目表编号不能为空")
  private String itemNumber;

  /** 项目名称 */
  @Excel(name = "项目名称", type = Excel.Type.IMPORT)
  @NotBlank(message = "价目表名称不能为空！")
  private String name;

  /** 项目分类编号 */
  @Excel(name = "项目分类编号", type = Excel.Type.IMPORT)
  @NotBlank(message = "项目分类编号不能为空！")
  private String tariffCategoryNumber;

  /** 项目分类名称 */
  @Excel(name = "项目分类名称", type = Excel.Type.IMPORT)
  @NotBlank(message = "项目分类名称不能为空！")
  private String tariffCategoryName;

  /** 英文名称 */
  @Excel(name = "项目英文名称", type = Excel.Type.IMPORT)
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
