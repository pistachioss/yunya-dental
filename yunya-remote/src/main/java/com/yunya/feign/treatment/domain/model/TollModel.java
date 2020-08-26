package com.yunya.feign.treatment.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 简介: 收费参数封装模型
 *
 * @author: chow
 * @date: 2020/8/25 09:43
 * @description:
 * @since: 1.0.0
 */
@ApiModel("收费参数封装模型")
@Data
@ToString
public class TollModel implements Serializable {

  /** 账单（开单）记录ID */
  @ApiModelProperty("账单（开单）记录ID ")
  private Integer orderRecordId;

  @ApiModelProperty("折扣类型（0-优惠；1-授权折扣）")
  private Byte discountType;

  /** 预付款账户 */
  private List<PrepaymentAccountModel> prepaymentAccountModels;

  /** 会员卡账户 */
  private List<MemberAccountModel> memberAccountModels;
}
