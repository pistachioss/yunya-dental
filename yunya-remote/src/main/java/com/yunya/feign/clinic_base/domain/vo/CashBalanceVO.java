package com.yunya.feign.clinic_base.domain.vo;

import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介: 现金结存信息VO
 *
 * @author: chow
 * @date: 2020/12/19 15:29
 * @description:
 * @since: 1.0.0
 */
@ApiModel("现金结存信息VO")
@Data
@ToString
public class CashBalanceVO implements Serializable {
  /** 现金结存ID */
  @ApiModelProperty("现金结存ID")
  private Integer id;
  /** 结存日期 */
  @Excel(name = "结存日期")
  @ApiModelProperty("结存日期")
  private String settlementDate;
  /** 期初现金结余 */
  @Excel(name = "期初现金结余")
  @ApiModelProperty("期初现金结余")
  private BigDecimal beginningBalanceCash;
  /** 期间现金收款 */
  @Excel(name = "期间现金收款")
  @ApiModelProperty("期间现金收款")
  private BigDecimal periodCollectionCash;
  /** 本日现金存款 */
  @Excel(name = "本日现金存款")
  @ApiModelProperty("本日现金存款")
  private BigDecimal depositedCash;
  /** 差额调整 */
  @Excel(name = "差额调整")
  @ApiModelProperty("差额调整")
  private BigDecimal balanceAdjustmentCash;
  /** 期末现金结余 */
  @Excel(name = "期末现金结余")
  @ApiModelProperty("期末现金结余")
  private BigDecimal endingBalanceCash;
  /** 结存人ID */
  @ApiModelProperty("结存人ID")
  private Integer balancerId;
  /** 结存人姓名 */
  @Excel(name = "结存人")
  @ApiModelProperty("结存人姓名")
  private String balancerName;
}
