package com.yunya.feign.tariff.domain.vo;

import com.yunya.framework.common.annation.Excel;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介: 基础价目表导出信息VO
 *
 * @author: chow
 * @date: 2020/8/5 17:02
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
public class BaseTariffExportVO implements Serializable {

  /** 项目编码 */
  @Excel(name = "项目编码")
  private String itemNumber;

  /** 项目名称 */
  @Excel(name = "项目名称")
  private String name;

  /** 项目分类名称 */
  @Excel(name = "项目分类名称")
  private String tariffCategoryName;

  /** 项目分类编号 */
  @Excel(name = "项目分类编号")
  private String tariffCategoryNumber;

  /** 英文名称 */
  @Excel(name = "项目英文名称")
  private String englishName;

  /** 单位 */
  @Excel(name = "单位")
  private String unit;

  /** 价格 */
  @Excel(name = "单价")
  private BigDecimal price;
}
