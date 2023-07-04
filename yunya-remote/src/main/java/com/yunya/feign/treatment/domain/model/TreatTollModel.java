package com.yunya.feign.treatment.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
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
public class TreatTollModel extends TreatTollDebtModel {

  /** 折扣方式（0-不使用优惠；1-优惠；）*/
  @ApiModelProperty(value = "折扣类型（0-不使用优惠；1-优惠；）", required = true)
  @NotNull(message = "优惠类型不能为空")
  private Byte discountType;

  /** 划扣卡核销项目列表 */
  @ApiModelProperty("划扣卡核销项目列表")
  private List<SwipeItemModel> swipeItemModels;
}
