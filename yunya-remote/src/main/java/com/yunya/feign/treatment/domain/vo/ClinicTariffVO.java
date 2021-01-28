package com.yunya.feign.treatment.domain.vo;

import com.yunya.framework.common.annation.Excel;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

/**
 * 简介: 门诊价目表信息VO
 *
 * @author: chow
 * @date: 2020/8/6 09:21
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
public class ClinicTariffVO implements Serializable {
  /** 门诊价目表ID */
  private Integer id;

  /** 诊所ID */
  private Integer orgId;

  /** 价目表分类ID */
  private Integer tariffCategoryId;

  /** 分类名称 */
  @Excel(name = "项目分类名称")
  private String tariffCategoryName;

  /** 价目表分类编号 */
  @Excel(name = "项目分类编号")
  private String tariffCategoryNumber;

  /** 基础价目表ID */
  private Integer tariffId;

  /** 基础价目表名称 */
  @Excel(name = "项目名称")
  private String name;

  /** 基础价目表名称 */
  @Excel(name = "项目英文名称")
  private String englishName;

  /** 基础价目表编号 */
  @Excel(name = "项目编码")
  private String number;

  /** 单位 */
  @Excel(name = "单位")
  private String unit;

  /** 门诊价目表单价 */
  @Excel(name = "单价")
  private BigDecimal price;

  /** 会员价 */
  private Map<Integer, Object> memberPrices;

  /** 是否启用 */
  private Boolean inservice;
}
