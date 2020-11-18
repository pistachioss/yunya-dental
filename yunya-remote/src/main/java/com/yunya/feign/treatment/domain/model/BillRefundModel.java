package com.yunya.feign.treatment.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 * 简介: 账单退费参数模型
 *
 * @author: chow
 * @date: 2020/9/21 17:34
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("账单退费参数模型")
public class BillRefundModel implements Serializable {
  /** 就诊记录ID */
  @ApiModelProperty(value = "就诊记录ID", required = true)
  @NotNull(message = "就诊记录ID")
  private Integer treatmentRecordId;
  /** 退费订单明细列表 */
  @NotEmpty(message = "退费订单明细列表不能为空")
  private List<RefundOrderDetailModel> refundOrderDetailModels;
  /** 退费会员账户信息 */
  private MemberRefundModel memberRefundModel;
  /** 退费预付款账户信息 */
  private PrepaymentRefundModel prepaymentRefundModel;
  /** 其他退费入账方式信息 */
  private List<PaymentModel> refundPaymentModels;
  /** 退费原因 */
  @ApiModelProperty("退费原因")
  @Size(max = 1000, message = "最多可输入1000个字符！")
  private String refundReason;
  /** 退附件列表 */
  @ApiModelProperty("退费附件列表")
  private List<String> refundAnnex;
}
