package com.yunya.feign.treatment.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介: 账单退费记录VO
 *
 * @author: chow
 * @date: 2020/11/3 14:15
 * @description:
 * @since: 1.0.0
 */
@ApiModel("账单退费记录VO")
@Data
@ToString
public class BillRefundRecordVO implements Serializable {
  /** 账单退费记录ID */
  @ApiModelProperty("账单退费记录ID")
  private Integer billRefundRecordId;
  /** 退费时间 */
  @ApiModelProperty("退费时间")
  private String billRefundDate;
  /** 组织ID */
  @ApiModelProperty("组织ID")
  private Integer orgId;
  /** 组织名称 */
  @ApiModelProperty("组织名称")
  private String orgName;
  /** 账单编号 */
  @ApiModelProperty("账单编号")
  private String billNum;
  /** 退费金额 */
  @ApiModelProperty("退费金额")
  private BigDecimal totalRefundAmount;
  /** 操作人ID */
  @ApiModelProperty("操作人ID")
  private Integer operateId;
  /** 操作人姓名 */
  @ApiModelProperty("操作人姓名")
  private String operateName;
  /** 退费原因 */
  @ApiModelProperty("退费原因")
  private String reason;
}
