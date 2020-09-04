package com.yunya.feign.treatment.domain.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介: 价目表开单管理信息VO
 *
 * @author: chow
 * @date: 2020/8/8 16:00
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
public class BaseTariffAssociationVO implements Serializable {
  /** 基础价目表ID */
  private Integer id;

  /** 价目表分类ID */
  private Integer tariffCategoryId;

  /** 价目表分类名称 */
  private String tariffCategoryName;

  /** 价目表编号 */
  private String tariffCategoryNumber;

  /** 项目编码 */
  private String itemNumber;

  /** 项目名称 */
  private String name;

  /** 项目英文名称 */
  private String englishName;

  /** 电子病历处理内容 */
  private String emr;

  /** 注意事项 */
  private String attention;

  /** 几天后随访 */
  private String fellowUp;

  /** 是否启用 */
  private Boolean inservice;
}
