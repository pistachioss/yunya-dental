package com.yunya.feign.treatment.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 简介: 价目表开单完成信息查询参数模型
 *
 * @author: chow
 * @date: 2020/12/29 16:14
 * @description:
 * @since: 1.0.0
 */
@ApiModel("专科项目价目表开单完成信息查询参数模型")
@Data
@ToString
public class SpecialistProjectTariffCompletedInfoQuery implements Serializable {
  /** 日期类型（0-月，1-年） */
  @ApiModelProperty(value = "日期类型（0-月，1-年）", required = true)
  @NotNull(message = "日期类型不能为空！")
  private Byte dateType;
  /** 查询日期 */
  @ApiModelProperty(value = "查询日期", required = true)
  @NotBlank(message = "查询日期不能为空！")
  private String queryDate;
  /** 价目表ID列表 */
  @ApiModelProperty(value = "价目表ID列表", required = true)
  private String[] tariffIds;
  /** 数据归属组织ID列表 */
  @ApiModelProperty(value = "数据归属组织ID列表", required = true)
  private Integer[] belongIds;
}
