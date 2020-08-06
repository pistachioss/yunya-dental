package com.yunya.feign.tariff.domain.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 描述: 商品分类信息VO
 *
 * @author GaoLuding
 * @create 2020-07-13 15:22
 */
@Data
@ToString
public class BaseOralTariffCategoryVO implements Serializable {
  private Integer id;

  /** 分类名称 */
  private String name;

  /** 商品分类信息编号 */
  private String number;

  /** 是否启用 */
  private Boolean inservice;
}
