package com.yunya.feign.system.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介: 入账方式信息VO
 *
 * @author: chow
 * @date: 2020/9/29 15:05
 * @description:
 * @since: 1.0.0
 */
@ApiModel("入账方式信息VO")
@Data
@ToString
public class AccountItemVO implements Serializable {
  @ApiModelProperty("门诊入账方式ID")
  private Integer id;
  /** 入账方式分类ID */
  @ApiModelProperty("入账方式分类ID")
  private Integer accountTypeId;
  /** 入账方式分类名称 */
  @ApiModelProperty("入账方式分类名称")
  private String accountTypeName;
  /** 公司端对应支付方式分类表的ID */
  @ApiModelProperty("公司端对应支付方式分类表的ID")
  private Integer accountItemId;
  /** 入账方式名称 */
  @ApiModelProperty("入账方式名称")
  private String accountItemName;
  /** 入账方式类型 */
  @ApiModelProperty("入账方式类型（0.现金,1.预售,2.优惠,3平台结算）")
  private Byte type;
  /** 是否启用 */
  @ApiModelProperty("是否启用0-不启用；1-启用")
  private Boolean inservice;
}
