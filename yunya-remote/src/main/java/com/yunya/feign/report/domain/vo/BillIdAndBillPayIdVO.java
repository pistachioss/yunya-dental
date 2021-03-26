package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 简介: 收费记录账单ID与订单ID组合类
 *
 * @author: chow
 * @date: 2021/3/16 11:14
 * @description:
 * @since: 1.0.0
 */
@ApiModel("收费记录账单ID与订单ID组合类")
@Data
@ToString
public class BillIdAndBillPayIdVO implements Serializable {
  /** 支付记录ID */
  @ApiModelProperty("支付记录ID")
  private Integer billPayId;
  /** 收费金额 */
  @ApiModelProperty("收费金额")
  private BigDecimal receivedAmount;
  /** 收费组织ID */
  @ApiModelProperty("收费组织ID")
  private Integer payeeOrgId;
  /** 收费时间 */
  @ApiModelProperty("收费时间")
  private Date payeeDate;
  /** 订单记录ID */
  @ApiModelProperty("订单记录ID")
  private Integer billId;
  /** 订单实收总额 */
  @ApiModelProperty("订单实收总额")
  private BigDecimal actualAmount;
  /** 组织ID */
  @ApiModelProperty("账单组织ID")
  private Integer billOrgId;
  /** 账单时间 */
  @ApiModelProperty("账单时间")
  private Date billDate;
  /** 使用优惠组织ID */
  @ApiModelProperty("使用优惠组织ID")
  private Integer privilegeOrgId;
  /** 使用优惠日期 */
  @ApiModelProperty("使用优惠日期")
  private Date privilegeDate;
  /** 是否首次优惠使用优惠 */
  @ApiModelProperty("是否首次优惠使用优惠")
  private Boolean firstPrivilege;
}
