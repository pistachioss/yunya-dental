package com.yunya.feign.treatment.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 简介: 账单付款记录VO
 *
 * @author: chow
 * @date: 2020/9/12 13:01
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("账单付款记录VO")
public class BillPayRecordVO implements Serializable {
  /** 收费记录ID */
  @ApiModelProperty("收费记录ID")
  private Integer billPayRecordId;
  /** 收费日期 */
  @ApiModelProperty("收费日期")
  private String chargeDate;
  /** 收费门诊ID */
  @ApiModelProperty("收费门诊ID")
  private Integer orgId;
  /** 门诊名称 */
  @ApiModelProperty("门诊名称")
  private String orgName;
  /** 收款人ID */
  @ApiModelProperty("收款人ID")
  private Integer payeeId;
  /** 收款人姓名 */
  @ApiModelProperty("收款人姓名")
  private String payeeName;
  /** 收款金额 */
  @ApiModelProperty("收款金额")
  private BigDecimal receivedAmount;
  /** 仍欠费金额 */
  @ApiModelProperty("仍欠费金额")
  private BigDecimal stillOweAmount;
  /** 付款明细 */
  private List<BillPayDetailRecordVO> billPayDetailRecords;
}
