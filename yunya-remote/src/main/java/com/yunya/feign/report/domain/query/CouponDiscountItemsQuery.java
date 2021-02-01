package com.yunya.feign.report.domain.query;

import com.yunya.framework.common.model.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 简介: 产品优惠项目明细查询参数模型
 *
 * @author: chow
 * @date: 2020/11/25 10:47
 * @description:
 * @since: 1.0.0
 */
@ApiModel("产品优惠项目明细查询参数模型")
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
public class CouponDiscountItemsQuery extends PageQuery implements Serializable {
  @ApiModelProperty("患者关键字-姓名/拼音姓名/手机号")
  private String keyWord;

  @ApiModelProperty("产品名称")
  private String couponName;

  @ApiModelProperty("卡号")
  private String cardNumber;

  @ApiModelProperty(value = "门诊ID",required = true)
  @NotNull(message = "请选择门诊！")
  private Integer orgId;

  @ApiModelProperty(value = "账单开始日期")
  private String orderStartDate;

  @ApiModelProperty(value = "账单结束日期")
  private String orderEndDate;

  @ApiModelProperty("项目分类")
  private Integer[] categoryIds;
}
