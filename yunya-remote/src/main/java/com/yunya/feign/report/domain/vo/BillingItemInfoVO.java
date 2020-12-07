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
 * @date: 2020/12/7 10:40
 * @description:
 * @since: 1.0.0
 */
@ApiModel("开单项目数量信息VO")
@Data
@ToString
public class BillingItemInfoVO implements Serializable {
  /** 项目ID */
  @ApiModelProperty("项目ID")
  private Integer itemId;
  /** 项目类型 */
  @ApiModelProperty("项目类型：0-价目表；1-商品")
  private Byte itemType;
  /** 项目编号 */
  @ApiModelProperty("项目编号")
  private String itemNum;
  /** 项目名称 */
  @ApiModelProperty("项目名称")
  private String itemName;
  /** 项目分类ID */
  @ApiModelProperty("项目分类ID")
  private Integer categoryId;
  /** 项目分类名称 */
  @ApiModelProperty("项目分类名称")
  private String categoryName;
  /** 开单数量合计 */
  @ApiModelProperty("开单数量合计")
  private Integer totalBillCount;
}
