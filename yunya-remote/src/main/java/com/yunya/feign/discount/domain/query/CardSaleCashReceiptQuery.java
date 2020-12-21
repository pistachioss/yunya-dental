package com.yunya.feign.discount.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 简介: 卡券售卖现金收款查询
 *
 * @author: chow
 * @date: 2020/12/19 13:42
 * @description:
 * @since: 1.0.0
 */
@ApiModel("卡券售卖现金收款查询参数模型")
@Data
@ToString
public class CardSaleCashReceiptQuery implements Serializable {
  /** 售出组织ID */
  @ApiModelProperty(value = "售出组织ID", required = true)
  @NotNull(message = "售出组织ID不能为空！")
  private Integer orgId;
  /** 开始时间 */
  @ApiModelProperty(value = "开始时间", example = "yyyy-MM-dd")
  private String startDate;
  /** 结束时间 */
  @ApiModelProperty(value = "结束时间", example = "yyyy-MM-dd", required = true)
  @NotBlank(message = "结束时间不能为空！")
  private String endDate;
}
