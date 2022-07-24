package com.yunya.feign.report.domain.query;

import com.yunya.framework.common.model.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 简介:
 *
 * @author: chow
 * @date: 2022/7/24 15:01
 * @description:
 * @since: 1.0.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("卡券使用记录参数模型")
public class CardConsumeQuery extends PageQuery {

  @ApiModelProperty(value = "激活开始日期", example = "2022-07-23", required = true)
  @NotBlank(message = "请选择激活开始日期")
  private String activationStartDate;

  @ApiModelProperty(value = "激活结束日期", example = "2022-07-24", required = true)
  @NotBlank(message = "请选择激活结束日期")
  private String activationEndDate;

  @ApiModelProperty(value = "销售渠道ID", required = true)
  @NotNull(message = "请选择销售渠道")
  @Length(min = 1,message = "非法请求参数")
  private Integer[] saleChannelIds;

  @ApiModelProperty("产品ID")
  @Min(value = 1, message = "非法请求参数")
  private Integer[] couponId;
}
