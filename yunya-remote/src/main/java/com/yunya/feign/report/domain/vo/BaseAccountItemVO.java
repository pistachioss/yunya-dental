package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介: 基础支付方式信息VO
 *
 * @author: chow
 * @date: 2020/12/11 10:55
 * @description:
 * @since: 1.0.0
 */
@ApiModel("基础支付方式信息VO")
@Data
@ToString
public class BaseAccountItemVO implements Serializable {
  /** 支付方式明细ID */
  @ApiModelProperty("支付方式明细ID")
  private Integer accountItemId;
  /** 支付方式名称 */
  @ApiModelProperty("支付方式名称")
  private String accountItemName;
}
