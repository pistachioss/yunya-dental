package com.yunya.feign.treatment.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 简介: 账单退费组合信息VO
 *
 * @author: chow
 * @date: 2020/9/27 19:20
 * @description:
 * @since: 1.0.0
 */
@ApiModel("账单退费组合信息VO")
@Data
@ToString
public class BillRefundGroupInfoVO implements Serializable {
  /** 退费记录ID */
  @ApiModelProperty("退费记录ID")
  private Integer billRefundRecordId;
  /** 退费开单记录详情列表 */
  private List<BillRefundOrderDetailVO> billRefundOrderDetails;
  /** 退费方式列表 */
  private List<BillRefundPaymentVO> billRefundPayments;
  /** 退费原因 */
  @ApiModelProperty("退费原因")
  private String reason;
  /** 退费附件 */
  @ApiModelProperty("退费附件")
  private String refundCertificate;
}
