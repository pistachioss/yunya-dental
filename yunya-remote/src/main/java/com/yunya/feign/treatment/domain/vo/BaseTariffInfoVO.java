package com.yunya.feign.treatment.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 简介: 基础价目表信息VO
 *
 * @author: chow
 * @date: 2020/8/5 16:04
 * @description:
 * @since: 1.0.0
 */
@ApiModel("基础价目表信息VO")
@Data
@ToString
public class BaseTariffInfoVO implements Serializable {
  /** 基础价目表ID */
  @ApiModelProperty("基础价目表ID")
  private Integer id;
  /** 价目表ID */
  @ApiModelProperty("价目表ID")
  private Integer tariffCategoryId;
  /** 价目表名称 */
  @ApiModelProperty("价目表名称")
  private String tariffCategoryName;
  /** 价目表编号 */
  @ApiModelProperty("价目表编号")
  private String tariffCategoryNumber;
  /** 项目编码 */
  @ApiModelProperty("项目编码")
  private String itemNumber;
  /** 项目名称 */
  @ApiModelProperty("项目名称")
  private String name;
  /** 拼音 */
  @ApiModelProperty("拼音")
  private String pinyin;
  /** 项目英文名称 */
  @ApiModelProperty("项目英文名称")
  private String englishName;
  /** 单位 */
  @ApiModelProperty("单位")
  private String unit;
  /** 价格 */
  @ApiModelProperty("价格")
  private BigDecimal price;
  /** 是否启用 */
  @ApiModelProperty("是否启用")
  private Boolean inservice;
  /** 门诊项目价格信息列表 */
  private List<ClinicItemPriceVO> clinicItemInfos;
}
