package com.yunya.feign.discount.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Min;
import java.io.Serializable;

/**
 * 简介: 产品分类查询参数模型
 *
 * @author: chow
 * @date: 2020/7/30 17:50
 * @description:
 * @since: 1.0.0
 */
@ApiModel("产品分类查询参数模型")
@Data
@ToString
public class ProductTypeQueryForm implements Serializable {
  @ApiModelProperty(value = "是否分页,默认分页")
  private Boolean whetherPage = true;

  @ApiModelProperty("页码，默认第1页")
  @Min(message = "最小值", value = 1)
  private Integer pageNum = 1;

  @ApiModelProperty("每页显示数量，默认显示10条")
  @Min(message = "最小值", value = 1)
  private Integer pageSize = 10;
  /** ID */
  @ApiModelProperty("ID")
  private Integer id;
  /** 产品分类名称 */
  @ApiModelProperty("产品分类名称")
  private String name;
  /** 是否启用 */
  @ApiModelProperty("是否启用")
  private Boolean inservice;

  @ApiModelProperty("产品类型（0-代金券；1-折扣券；2-兑换券；3-套餐券；4-充值券; 5-划扣券）")
  private Integer type;

  @ApiModelProperty("是否线上售卖(0:否 1:是)")
  private Boolean isOnlineSale;
}
