package com.yunya.modules.system.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel("会员卡类型VO模型")
@Data
@ToString
public class MemberTypeVO implements Serializable {
  @ApiModelProperty("会员卡类型ID")
  private Integer id;
  /** 会员卡名称 */
  @ApiModelProperty("会员卡名称")
  private String name;
  /** 续费金额 */
  @ApiModelProperty("续费金额")
  private BigDecimal renewalAmount;
  /** 年限 */
  @ApiModelProperty("年限（续费有效期）")
  private Integer ageLimit;
  /** 折扣率（价目表自动调价的折扣率） */
  @ApiModelProperty("折扣率（价目表自动调价的折扣率）")
  private Float rate;
  /** 类型,0:普通,1:VIP */
  @ApiModelProperty("类型,0:普通,1:VIP")
  private Byte type;
  /** 会员卡图标 */
  @ApiModelProperty("会员卡图标")
  private Byte icon;
  /** 会员卡图片样式获取码 */
  @ApiModelProperty("会员卡图片样式获取码")
  private String pictureCode;
  /** 会员卡描述（青藤、银藤、金藤、艾维会员） */
  @ApiModelProperty("会员卡描述（青藤、银藤、金藤、艾维会员）")
  private String description;
  /** 是否启用 */
  @ApiModelProperty("是否有效（0-否；1-是）")
  private Boolean inservice;
}
