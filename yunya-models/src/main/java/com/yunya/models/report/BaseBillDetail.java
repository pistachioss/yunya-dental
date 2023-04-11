package com.yunya.models.report;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

@Data
@Table(name = "base_bill_detail")
public class BaseBillDetail {
  /** 账单明细ID */
  @Id
  @Column(name = "bill_detail_id")
  private Integer billDetailId;

  /** 组织ID */
  @Column(name = "org_id")
  private Integer orgId;

  /** 账单ID */
  @Column(name = "bill_id")
  private Integer billId;

  /** 折扣金额(多种优惠方式) */
  @Column(name = "discount_amount")
  private BigDecimal discountAmount;

  /** 卡券单个工作量 */
  @Column(name = "coupon_workload")
  private BigDecimal couponWorkload;

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
  private Integer itemName;

  /** 项目类型（0-价目表；1-商品） */
  @Column(name = "item_type")
  private Byte itemType;

  /** 添加来源（0-医生；1-前台） */
  @Column(name = "source_type")
  private Byte sourceType;

  /** 数量 */
  private Integer quantity;

  /** 单价 */
  private BigDecimal price;

  /** 项目收费金额（含免单） */
  @Column(name = "received_amount")
  private BigDecimal receivedAmount;

  /** 项目免单金额 */
  @Column(name = "free_amount")
  private BigDecimal freeAmount;

  /** 开单备注 */
  private String remark;
}
