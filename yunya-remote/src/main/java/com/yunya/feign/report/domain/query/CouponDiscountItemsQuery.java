package com.yunya.feign.report.domain.query;

import com.yunya.framework.common.model.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
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

  @ApiModelProperty("门诊ID")
  private Integer orgId;

  @ApiModelProperty(value = "账单开始日期", example = "yyyy-MM-dd", required = true)
  @NotBlank(message = "账单开始日期不能为空！")
  private String orderStartDate;

  @ApiModelProperty(value = "账单结束日期", required = true, example = "yyyy-MM-dd")
  @NotBlank(message = "账单结束日期不能为空！")
  private String orderEndDate;

  @ApiModelProperty("项目分类")
  private Integer[] categoryIds;
}
