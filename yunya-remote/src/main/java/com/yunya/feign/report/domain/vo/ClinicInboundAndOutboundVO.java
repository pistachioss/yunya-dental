package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 简介: 门诊出入账信息VO
 *
 * @author: chow
 * @date: 2020/12/15 13:30
 * @description:
 * @since: 1.0.0
 */
@ApiModel("门诊出入账信息VO")
@Data
@ToString
public class ClinicInboundAndOutboundVO implements Serializable {
  /** 收支明细分类 */
  @ApiModelProperty(
      "收支明细分类:0-账单收费；1-收欠费；2-会员充值；3-预付款充值；4-产品售出；5-诊所代收；6-账单退费；7-会员卡退费；8-预付款退费；9-诊所被代收")
  private Byte type;
  /** 收支明细分类名称 */
  @ApiModelProperty("收支明细分类名称")
  private String name;
  /** 收支明细支付方式列表 */
  private List<BoundPaymentVO> paymentInfoList;
}
