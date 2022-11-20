package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author xiangyang
 * @date 2020/11/16
 */
@Data
@ApiModel(value = "礼包详情")
public class BenefitItemVo {
	@ApiModelProperty("礼包名称")
	private String couponName;
	@ApiModelProperty("项目id")
	private Integer itemId;
	@ApiModelProperty("项目编号")
	private String itemNo;
	@ApiModelProperty("项目名称")
	private String itemName;
	@ApiModelProperty("单位")
	private String itemUnit;
	@ApiModelProperty("单价")
	private BigDecimal itemPrice;
	@ApiModelProperty("数量（数量为-1，代表不限次数，固定写死的项目）")
	private Integer originalQuantity;
	@ApiModelProperty("剩余可使用数量（数量为-1，代表不限次数，固定写死的项目）")
	private Integer remainingQuantity;
	@ApiModelProperty("app是否显示")
	private Integer isShowApp;

}
