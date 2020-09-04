package com.yunya.feign.treatment.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Min;
import java.io.Serializable;

/**
 * 简介: 价目表分类查询条件模型
 *
 * @author: chow
 * @date: 2020/8/2 11:04
 * @description:
 * @since: 1.0.0
 */
@ApiModel("价目表分类查询条件模型")
@Data
@ToString
public class BaseTariffCategoryQueryForm implements Serializable {
  @ApiModelProperty(value = "是否分页,默认分页")
  private Boolean whetherPage = true;

  @ApiModelProperty("页码，默认第1页")
  @Min(message = "最小值", value = 1)
  private Integer pageNum = 1;

  @ApiModelProperty("每页显示数量，默认显示10条")
  @Min(message = "最小值", value = 1)
  private Integer pageSize = 10;

  @ApiModelProperty("价目表分类ID")
  private Integer id;

  @ApiModelProperty("价目表分类名称/价目表分类编号")
  private String keyWord;

  @ApiModelProperty("是否启用")
  private Boolean inservice;
}
