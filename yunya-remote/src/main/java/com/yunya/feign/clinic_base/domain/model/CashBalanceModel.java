package com.yunya.feign.clinic_base.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介: 现金结存新增模型
 *
 * @author: chow
 * @date: 2020/12/18 17:31
 * @description:
 * @since: 1.0.0
 */
@ApiModel("现金结存新增模型")
@Data
@ToString
public class CashBalanceModel implements Serializable {
  /** 结存日期 */
  @ApiModelProperty(value = "结存日期", required = true, example = "yyyy-MM-dd")
  @NotBlank(message = "结存日期不能为空！")
  private String settlementDate;
  /** 当前现金存款 */
  @ApiModelProperty(value = "当前现金存款", required = true)
  @NotNull(message = "本日现金存款不能为空！")
  @Min(value = 0, message = "现金存款不能小于0！")
  private BigDecimal depositedCash;
  /** 差额调整 */
  @ApiModelProperty("差额调整")
  private BigDecimal balanceAdjustment;
  /** 差额调整原因 */
  @ApiModelProperty("差额调整原因")
  private String adjustRemark;
  /** 结存凭证 */
  @ApiModelProperty("结存凭证列表")
  private String[] certificates;
}
