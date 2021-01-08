package com.yunya.feign.treatment.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * 简介: 账单入账现金收款查询
 *
 * @author: chow
 * @date: 2020/12/19 13:42
 * @description:
 * @since: 1.0.0
 */
@ApiModel("账单入账现金收款查询")
@Data
@ToString
public class CreditCashReceiptQuery implements Serializable {

  /** 支付账户id */
  @ApiModelProperty(value = "支付账户id", required = true)
  @NotNull(message = "支付账户id不能为空！")
  private Integer payId;
  /** 售出组织ID */
  @ApiModelProperty(value = "售出组织ID", required = true)
  @NotNull(message = "售出组织ID不能为空！")
  private Integer orgId;
  /** 开始时间 */
  @ApiModelProperty(value = "开始时间", example = "yyyy-MM-dd")
  private Date startDate;
  /** 结束时间 */
  @ApiModelProperty(value = "结束时间", example = "yyyy-MM-dd", required = true)
  @NotNull(message = "结束时间不能为空！")
  private Date endDate;
}
