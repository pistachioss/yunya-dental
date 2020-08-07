package com.yunya.feign.tariff.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 简介: 门诊价目表信息查询参数模型
 *
 * @author: chow
 * @date: 2020/8/6 12:45
 * @description:
 * @since: 1.0.0
 */
@ApiModel("门诊价目表信息查询参数模型")
@Data
@ToString
public class ClinicTariffQueryForm implements Serializable {
  @ApiModelProperty(value = "是否分页,默认true")
  private Boolean whetherPage = true;

  @ApiModelProperty("页码，默认第1页")
  @Min(message = "最小值", value = 1)
  private Integer pageNum = 1;

  @ApiModelProperty("每页显示数量，默认显示10条")
  @Min(message = "最小值", value = 1)
  private Integer pageSize = 10;

  @ApiModelProperty(value = "组织ID", required = true)
  @NotNull(message = "组织ID不能为空！")
  private Integer orgId;

  @ApiModelProperty(value = "价目表分类ID")
  private Integer tariffCategoryId;

  @ApiModelProperty("基础价目表ID")
  private Integer tariffId;

  @ApiModelProperty("门诊价目表ID")
  private Integer id;

  @ApiModelProperty("价目表名称/价目表编号/拼音缩写")
  private String keyWord;

  @ApiModelProperty("是否启用")
  private Boolean inservice;
}
