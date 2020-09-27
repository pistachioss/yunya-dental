package com.yunya.feign.treatment.domain.form;

import com.yunya.feign.treatment.domain.model.PaymentModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Set;

/**
 * 简介: 入账明细调整参数模型
 *
 * @author: chow
 * @date: 2020/9/15 13:04
 * @description:
 * @since: 1.0.0
 */
@ApiModel("入账明细调整参数模型")
@Data
@ToString
public class BillPayDetailForm implements Serializable {

  /** 入账明细列表 */
  @NotEmpty(message = "入账明细不能为空！")
  private Set<PaymentModel> paymentModels;

  @ApiModelProperty("调整原因")
  private String remark;
}
