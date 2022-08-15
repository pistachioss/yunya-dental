package com.yunya.models.report;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

@Data
@Table(name = "base_refund_detail")
public class BaseRefundDetail {
  /** 退费明细ID */
  @Id
  @Column(name = "refund_detail_id")
  private Integer refundDetailId;

  /** 退费记录ID */
  @Column(name = "refund_id")
  private Integer refundId;

  /** 订单明细ID */
  @Column(name = "bill_detail_id")
  private Integer billDetailId;

  /** 退费金额 */
  @Column(name = "refund_amount")
  private BigDecimal refundAmount;

  /** 执行人ID */
  @Column(name = "executor_id")
  private Integer executorId;

  /** 咨询师ID */
  @Column(name = "consulter_id")
  private Integer consulterId;

  /** 项目ID */
  @Column(name = "item_id")
  private Integer itemId;
  /** 项目名称 */
  @Column(name = "item_name")
  private String itemName;
  /** 项目类型（0-价目表；1-商品） */
  @Column(name = "item_type")
  private Byte itemType;
}
