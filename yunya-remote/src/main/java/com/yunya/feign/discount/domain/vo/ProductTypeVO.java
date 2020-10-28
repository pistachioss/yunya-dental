package com.yunya.feign.discount.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介: 产品分类VO
 *
 * @author: chow
 * @date: 2020/7/30 17:25
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
public class ProductTypeVO implements Serializable {
  /** 产品分类ID */
  @ApiModelProperty("产品分类ID")
  private Integer id;

  /** 分类名称 */
  @ApiModelProperty("分类名称")
  private String name;

  /** 分类下产品数量 */
  @ApiModelProperty("分类下产品数量")
  private Integer productNum;

  /** 是否启用 */
  @ApiModelProperty("是否启用")
  private Boolean inservice;

  /** 创建人 */
  private Integer crtId;

  /** 创建人姓名 */
  private String crtName;

  /** 创建时间 */
  private String crtTime;
}
