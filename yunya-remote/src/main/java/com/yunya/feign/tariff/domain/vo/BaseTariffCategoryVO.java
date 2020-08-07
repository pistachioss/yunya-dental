package com.yunya.feign.tariff.domain.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 描述: 价目表分类信息VO
 *
 * @author GaoLuding
 * @create 2020-07-13 14:45
 */
@Data
@ToString
public class BaseTariffCategoryVO implements Serializable {
  /** 价目表分类ID */
  private Integer id;

  /** 分类名称 */
  private String name;

  /** 价目表分类编号 */
  private String number;

  /** 是否启用 */
  private Boolean inservice;
}
