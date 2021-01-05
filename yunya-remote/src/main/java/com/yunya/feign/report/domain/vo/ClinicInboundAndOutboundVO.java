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
      "收支明细分类:0-账单收费（本月）；1-收欠费（本月）；2-收欠费（非本月）；3-会员充值；4-预付款充值；5-产品售出；"
          + "6-诊所代收（本月）；7-诊所代收（非本月）；8-账单退费（本月）；9-账单退费（非本月）；"
          + "10-会员卡退费；11-预付款退费；12-诊所被代收账（本月）；13-诊所被代收帐（非本月）")
  private Byte type;
  /** 收支明细分类名称 */
  @ApiModelProperty("收支明细分类名称")
  private String name;
  /** 收支明细支付方式列表 */
  private List<StatementPaymentVO> paymentInfoList;
}
