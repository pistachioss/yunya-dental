package com.yunya.modules.system.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介: 会员卡类型VO模型
 *
 * @author: chow
 * @date: 2020/7/24 09:41
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
public class MemberTypeVO implements Serializable {
  private Integer id;
  /** 会员卡名称 */
  private String name;
  /** 续费金额 */
  private BigDecimal renewalAmount;
  /** 年限 */
  private Integer ageLimit;
  /** 折扣率（价目表自动调价的折扣率） */
  private Float rate;
  /** 类型,0:普通,1:VIP */
  private Byte type;
  /** 会员卡图标 */
  private Byte icon;
  /** 会员卡描述（青藤、银藤、金藤、艾维会员） */
  private String description;
  /** 是否启用 */
  private Boolean inservice;
}
