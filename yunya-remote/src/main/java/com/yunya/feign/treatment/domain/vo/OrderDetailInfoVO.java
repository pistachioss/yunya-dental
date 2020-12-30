package com.yunya.feign.treatment.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel("开单详情信息VO")
@Data
@ToString
public class OrderDetailInfoVO implements Serializable {
  /** 开单记录ID */
  @ApiModelProperty("开单记录ID")
  private Integer orderRecordId;
  /** 开单总额 */
  @ApiModelProperty("开单总额")
  private BigDecimal totalAmount;
  /** 本单优惠金额 */
  @ApiModelProperty("本单优惠金额")
  private BigDecimal privilegeAmount;
  /** 开单状态 */
  @ApiModelProperty("订单状态（0-账单未锁定 ；1-账单锁定；2-结算完成状态；3-收费中）")
  private Byte status;
  /** 配诊助手列表 */
  private List<AssistantInfoVO> assistants;
  /** 开单详情列表 */
  private List<OrderDetailVO> orderDetails;
}
