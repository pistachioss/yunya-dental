package com.yunya.feign.treatment.domain.query;

import com.yunya.feign.treatment.domain.model.GeneralDiscountModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 简介: 订单优惠匹配查询参数模型
 *
 * @author: chow
 * @date: 2020/11/5 14:46
 * @description:
 * @since: 1.0.0
 */
@ApiModel("订单优惠匹配查询参数模型")
@Data
@ToString
public class OrderPrivilegeQuery implements Serializable {
  /** 订单记录ID */
  @ApiModelProperty(value = "订单记录ID", required = true)
  @NotNull(message = "订单记录ID不能为空！")
  private Integer orderRecordId;
  /** 折扣方式（0-不使用优惠；1-优惠）*/
  @ApiModelProperty(value = "折扣类型（0-不使用优惠；1-优惠）", required = true)
  @NotNull(message = "优惠类型不能为空")
  private Byte discountType;
  /** 卡券优惠信息 */
  @ApiModelProperty("卡券优惠信息")
  private GeneralDiscountModel generalDiscountModel;
}
