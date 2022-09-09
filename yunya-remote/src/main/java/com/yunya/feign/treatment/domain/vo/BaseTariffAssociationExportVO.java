package com.yunya.feign.treatment.domain.vo;

import com.yunya.framework.common.annation.Excel;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介: 价目表开单关联导出信息VO
 *
 * @author: chow
 * @date: 2020/8/8 16:48
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
public class BaseTariffAssociationExportVO implements Serializable {

  private Integer id;

  /** 项目编码 */
  @Excel(name = "项目编号")
  private String itemNumber;

  /** 项目名称 */
  @Excel(name = "项目名称")
  private String name;

  /** 价目表编号 */
  @Excel(name = "项目分类编号")
  private String tariffCategoryNumber;

  /** 价目表分类名称 */
  @Excel(name = "项目分类名称")
  private String tariffCategoryName;

  /** 电子病历处理内容 */
  @Excel(name = "电子病历处理内容")
  private String emr;

  /** 注意事项 */
  @Excel(name = "注意事项")
  private String attention;

  /** 几天后随访 */
  @Excel(name = "几天后随访")
  private String fellowUp;
}
