package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介: 开单项目信息VO
 *
 * @author: chow
 * @date: 2020/12/7 09:42
 * @description:
 * @since: 1.0.0
 */
@ApiModel("开单项目信息VO")
@Data
@ToString
public class ItemInfoVO implements Serializable {
  /** 项目ID */
  @ApiModelProperty(value = "项目ID")
  private Integer itemId;
  /** 分类类型：0-价目表；1-商品 */
  @ApiModelProperty(value = "分类类型：0-价目表；1-商品")
  private Byte itemType;
  /** 项目名称 */
  @ApiModelProperty("项目名称")
  private String itemName;
  /** 项目编号 */
  @ApiModelProperty("项目编号")
  private String itemNum;
}
