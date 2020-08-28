package com.yunya.feign.treatment.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 简介: 发票信息参数模型
 *
 * @author: chow
 * @date: 2020/8/27 17:17
 * @description:
 * @since: 1.0.0
 */
@ApiModel("发票信息参数模型")
@Data
@ToString
public class InvoiceModel implements Serializable {

  /** 是否开票 */
  @ApiModelProperty(value = "是否开票（0-否；1-是）", required = true)
  @NotNull(message = "是否开票不能为空！")
  private Boolean invoice;

  @ApiModelProperty("发票编号（开票时必传）")
  @Size(max = 32, message = "发票编号不能超过32个字符！")
  private String invoiceNumber;
}
