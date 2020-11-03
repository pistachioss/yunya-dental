package com.yunya.feign.treatment.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 描述: 商品分类信息VO
 *
 * @author GaoLuding
 * @create 2020-07-13 15:22
 */
@ApiModel("商品分类信息VO")
@Data
@ToString
public class BaseOralTariffCategoryVO implements Serializable {
  @ApiModelProperty("商品分类id")
  private Integer id;
  /** 分类名称 */
  @ApiModelProperty("商品分类名称")
  private String name;
  /** 商品分类信息编号 */
  @ApiModelProperty("商品分类信息编号")
  private String number;
  /** 是否启用 */
  @ApiModelProperty("是否启用；0-否；1-是")
  private Boolean inservice;
}
