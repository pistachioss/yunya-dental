package com.yunya.feign.tariff.domain.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介: 门诊项目（商品/价目表）价格信息
 *
 * @author: chow
 * @date: 2020/8/3 17:06
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
public class ClinicItemPriceVO implements Serializable {
  /** 门诊商品ID */
  private Integer clinicItemId;

  /** 公司端对应的组织ID */
  private Integer orgId;

  /** 组织名称 */
  private String orgName;

  /** 公司端对应表的ID */
  private Integer clinicTariffId;

  /** 单价 */
  private BigDecimal clinicItemPrice;

  /** 是否启用 */
  private Boolean itemInservice;
}
