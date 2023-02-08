package com.yunya.feign.patient_central.domain.vo.web;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 简单介绍:</br> 返回会员信息模型
 *
 * @author: WY
 * @date 2020/7/30 13:53
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("返回会员信息模型")
public class MemberBaseInfoVo implements Serializable {

  /** 主键id */
  @ApiModelProperty("会员卡ID")
  private Integer id;

  /** 诊所id */
  @ApiModelProperty("诊所id")
  private Integer orgId;

  /** 患者id */
  @ApiModelProperty("患者id")
  private Integer patientId;

  /** 患者名称 */
  @ApiModelProperty("患者名称")
  private String name;

  /** 会员卡卡号 */
  @ApiModelProperty("会员卡卡号")
  private String cardNumber;

  /** 会员卡类型id */
  @ApiModelProperty("会员卡类型id")
  private Integer memberTypeId;

  /** 会员卡类型名称 */
  @ApiModelProperty("会员卡类型名称")
  private String memberCardName;

  /** 会员本卡总余额（本金+赠金） */
  @ApiModelProperty("会员本卡总余额（本金+赠金）")
  private BigDecimal memberCardMoneySum;

  /** 本金 */
  @ApiModelProperty("本金")
  private BigDecimal principalAmount;

  /** 赠金 */
  @ApiModelProperty("赠金")
  private BigDecimal bonusAmount;

  /** 开卡日期 */
  private Date crtTime;

  @ApiModelProperty("积分")
  private Integer point;

  /**
   * 备注（账户余额）
   */
  @ApiModelProperty("备注（账户余额）")
  private String remark;
}
