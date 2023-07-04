package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 简介: 账单工作量VO
 *
 * @author: chow
 * @date: 2021/3/22 11:16
 * @description:
 * @since: 1.0.0
 */
@ApiModel("账单工作量VO")
@Data
@ToString
public class BillRecordWorkloadVO implements Serializable {
  /** 订单ID */
  @ApiModelProperty("订单ID")
  private Integer billId;
  /** 订单工作量合计 */
  @ApiModelProperty("订单工作量合计")
  private BigDecimal billTotalWorkload;
  /** 订单非工作量合计 */
  @ApiModelProperty("订单非工作量合计")
  private BigDecimal billTotalNotWorkload;
  /** 订单补入工作量 */
  @ApiModelProperty("订单补入工作量")
  private BigDecimal billTotalCouponWorkload;
  /** 账单门诊ID */
  @ApiModelProperty("账单门诊ID")
  private Integer billOrgId;
  /** 使用优惠门诊ID */
  @ApiModelProperty("使用优惠门诊ID")
  private Integer privilegeOrgId;
  /** 是否首次收费进行优惠 */
  @ApiModelProperty("是否首次收费进行优惠")
  private Boolean firstPrivilege;
  /** 使用优惠日期*/
  private Date privilegeDate;
  /** 月份*/
  private String month;
}
