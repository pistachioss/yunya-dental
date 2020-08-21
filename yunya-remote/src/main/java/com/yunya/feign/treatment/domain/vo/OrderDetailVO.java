package com.yunya.feign.treatment.domain.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介: 开单详情VO
 *
 * @author: chow
 * @date: 2020/8/21 10:06
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
public class OrderDetailVO implements Serializable {
  /** 开单详情ID */
  private Integer orderDetailId;

  /** 开单类型（0-价目表；1-商品；） */
  private Byte type;

  /** 开单项目ID */
  private Integer billingItemId;

  /** 开单项目名称 */
  private String billingItemName;

  /** 单位 */
  private String unit;

  /** 门诊价目表单价 */
  private BigDecimal price;

  /** 数量 */
  private Integer quantity;

  /** 应收原价合计 */
  private BigDecimal receivableAmount;

  /** 牙位 */
  private String toothBit;

  /** 执行人ID */
  private Integer executorId;

  /** 执行人姓名 */
  private String executorName;

  /** 备注 */
  private String remarks;
}
