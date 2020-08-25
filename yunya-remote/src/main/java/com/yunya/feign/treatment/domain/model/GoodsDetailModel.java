package com.yunya.feign.treatment.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 简介: 商品明细添加参数模型
 *
 * @author: chow
 * @date: 2020/8/25 10:27
 * @description:
 * @since: 1.0.0
 */
@ApiModel("商品明细添加参数模型")
@Data
@ToString
public class GoodsDetailModel implements Serializable {

  @ApiModelProperty(value = "账单（开单）记录ID", required = true)
  @NotNull(message = "开单记录ID不能为空！")
  private Integer orderRecordId;

  /** 商品明细 */
  private List<OrderDetailModel> orderDetails;
}
