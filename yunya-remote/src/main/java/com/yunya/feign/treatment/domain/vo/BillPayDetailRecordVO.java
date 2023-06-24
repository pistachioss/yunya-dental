package com.yunya.feign.treatment.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 简介: 账单入账明细VO
 *
 * @author: chow
 * @date: 2020/9/12 15:47
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("账单入账明细VO")
public class BillPayDetailRecordVO implements Serializable {

  /** 订单记录id */
  @ApiModelProperty("订单记录id")
  private Integer orderRecordId;
  /** 账单支付记录ID */
  @ApiModelProperty("账单支付记录ID")
  private Integer billPayRecordId;
  /** 账单支付明细ID */
  @ApiModelProperty("账单支付明细ID")
  private Integer billPayDetailRecordId;
  /** 入账方式ID */
  @ApiModelProperty("入账方式ID")
  private Integer accountItemId;
  /** 入账方式名称 */
  @ApiModelProperty("入账方式名称")
  private String accountItemName;
  /** 入账金额 */
  @ApiModelProperty("入账金额")
  private BigDecimal amount;
  /** 卡号 */
  @ApiModelProperty("预付款/会员卡账号")
  private String remark;
  /** 创建时间 */
  @ApiModelProperty("创建时间")
  private Date crtTime;
  /** 是否末次收费 */
  @ApiModelProperty("是否末次收费")
  private Boolean isLastCharge;
}
