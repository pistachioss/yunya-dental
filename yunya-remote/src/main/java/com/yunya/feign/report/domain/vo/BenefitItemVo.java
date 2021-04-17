package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author xiangyang
 * @date 2020/11/16
 */
@Data
@ApiModel(value = "礼包详情")
public class BenefitItemVo {
	@ApiModelProperty("项目id")
	private Integer itemId;
	@ApiModelProperty("项目名称")
	private String itemName;
	@ApiModelProperty("数量（数量为-1，代表不限次数，固定写死的项目）")
	private Integer originalQuantity;
	@ApiModelProperty("剩余可使用数量（数量为-1，代表不限次数，固定写死的项目）")
	private Integer remainingQuantity;
}
