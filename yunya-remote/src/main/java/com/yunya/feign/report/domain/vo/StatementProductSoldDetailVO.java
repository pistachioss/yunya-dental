package com.yunya.feign.report.domain.vo;

import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介: 产品售出明细VO
 *
 * @author: chow
 * @date: 2021/1/11 16:12
 * @description:
 * @since: 1.0.0
 */
@ApiModel("对账单产品售出明细VO")
@Data
@ToString
public class StatementProductSoldDetailVO implements Serializable {
  /** 售出日期 */
  @Excel(name = "售出日期")
  @ApiModelProperty("售出日期")
  private String soldDate;
  /** 售出对象 */
  @Excel(name = "售出对象")
  @ApiModelProperty("售出对象")
  private String soldTargetName;
  /** 售出对象手机号 */
  @Excel(name = "售出对象手机号")
  @ApiModelProperty("售出对象手机号")
  private String soldTargetMobile;
  /** 产品名称 */
  @Excel(name = "产品名称")
  @ApiModelProperty("产品名称")
  private String productName;
  /** 产品类型 */
  @Excel(name = "产品类型")
  @ApiModelProperty("产品类型:0-代金券；1-折扣券；2-兑换券；3-套餐券；4-充值券")
  private Byte productType;
  /** 卡ID */
  @ApiModelProperty("卡ID")
  private Integer cardId;
  /** 卡号 */
  @Excel(name = "卡号")
  @ApiModelProperty("卡号")
  private String cardNum;
  /** 售出金额 */
  @Excel(name = "售出金额")
  @ApiModelProperty("售出金额")
  private BigDecimal soldAmount;
  /** 现金 */
  @Excel(name = "现金", scale = 2)
  @ApiModelProperty("现金")
  private BigDecimal cashAmount;
  /** 支付宝 */
  @Excel(name = "支付宝", scale = 2)
  @ApiModelProperty("支付宝")
  private BigDecimal aliPayAmount;
  /** 微信 */
  @Excel(name = "微信", scale = 2)
  @ApiModelProperty("微信")
  private BigDecimal weChatAmount;
  /** 银行账户 */
  @Excel(name = "银行账户", scale = 2)
  @ApiModelProperty("银行账户")
  private BigDecimal bankAmount;
  /** 售出人ID */
  @ApiModelProperty("售出人ID")
  private Integer soldOperatorId;
  /** 售出人 */
  @Excel(name = "售出人")
  @ApiModelProperty("售出人")
  private String soldOperatorName;
}
