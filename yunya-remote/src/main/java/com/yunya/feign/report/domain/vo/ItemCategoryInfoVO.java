package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 简介: 开单项目分类信息VO
 *
 * @author: chow
 * @date: 2020/12/7 09:42
 * @description:
 * @since: 1.0.0
 */
@ApiModel("开单项目分类信息VO")
@Data
@ToString
public class ItemCategoryInfoVO implements Serializable {
  /** 项目分类ID */
  @ApiModelProperty(value = "分类ID", required = true)
  private Integer categoryId;
  /** 分类类型 */
  @ApiModelProperty(value = "分类类型：0-价目表；1-商品", required = true)
  private Byte itemType;
  /** 项目分类名称 */
  @ApiModelProperty("分类名称")
  private String categoryName;
  /** 项目列表*/
  @ApiModelProperty("项目列表")
  private List<ItemInfoVO> itemInfoVOS;
}
