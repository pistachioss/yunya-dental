package com.yunya.feign.treatment.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介: 开单项目VO
 *
 * @author: chow
 * @date: 2020/8/21 10:06
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("开单项目VO")
public class ClinicTariffOrderVO implements Serializable {
  /** 门诊ID */
  @ApiModelProperty("门诊ID")
  private Integer orgId;

  /** 开单类型（0-价目表；1-商品；） */
  @ApiModelProperty("开单类型（0-价目表；1-商品；）")
  private Byte type;

  /** 开单项目ID */
  @ApiModelProperty("开单项目ID")
  private Integer billingItemId;

  /** 应收原价合计 */
  @ApiModelProperty("应收原价合计")
  private BigDecimal amount;
}
