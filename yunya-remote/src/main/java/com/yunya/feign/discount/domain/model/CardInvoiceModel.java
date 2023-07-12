package com.yunya.feign.discount.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@ApiModel("发票信息参数模型")
@Data
@ToString
public class CardInvoiceModel implements Serializable {

  /** 是否开票 */
  @ApiModelProperty(value = "是否开票（0-否；1-是）", required = true)
  @NotNull(message = "是否开票不能为空！")
  private Boolean invoice;

  @ApiModelProperty("发票编号（开票时必传）")
  @Size(max = 32, message = "发票编号不能超过32个字符！")
  private String invoiceNumber;
}
