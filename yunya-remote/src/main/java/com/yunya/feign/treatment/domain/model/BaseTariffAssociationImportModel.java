package com.yunya.feign.treatment.domain.model;

import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 简介: 价目表开单关联信息导入参数模型
 *
 * @author: chow
 * @date: 2020/8/8 16:45
 * @description:
 * @since: 1.0.0
 */
@ApiModel("价目表开单关联信息导入参数模型")
@Data
@ToString
public class BaseTariffAssociationImportModel implements Serializable {

  /** 项目编码 */
  @Excel(name = "项目编码", type = Excel.Type.IMPORT)
  @NotBlank(message = "项目编码不能为空！")
  private String itemNumber;

  /** 项目名称 */
  @Excel(name = "项目名称", type = Excel.Type.IMPORT)
  @NotBlank(message = "项目名称不能为空！")
  private String name;

  /** 价目表编号 */
  @Excel(name = "项目分类编号", type = Excel.Type.IMPORT)
  @NotBlank(message = "项目分类编号不能为空！")
  private String tariffCategoryNumber;

  /** 价目表分类名称 */
  @Excel(name = "项目分类名称", type = Excel.Type.IMPORT)
  @NotBlank(message = "项目分类名称不能为空！")
  private String tariffCategoryName;

  /** 电子病历处理内容 */
  @Excel(name = "电子病历处理内容", type = Excel.Type.IMPORT)
  private String emr;

  /** 注意事项 */
  @Excel(name = "注意事项", type = Excel.Type.IMPORT)
  private String attention;

  /** 几天后随访 */
  @Excel(name = "几天后随访", type = Excel.Type.IMPORT)
  private String fellowUp;
}
