package com.yunya.feign.treatment.domain.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 简介: 开单详情信息VO
 *
 * @author: chow
 * @date: 2020/8/20 19:35
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
public class OrderDetailInfoVO implements Serializable {

  /** 开单记录ID */
  private Integer orderRecordId;
  /** 开单总额 */
  private BigDecimal totalAmount;
  /** 开单状态 */
  private Byte status;
  /** 配诊助手列表 */
  private List<AssistantInfoVO> assistants;
  /** 开单详情列表 */
  private List<OrderDetailVO> orderDetails;
}
