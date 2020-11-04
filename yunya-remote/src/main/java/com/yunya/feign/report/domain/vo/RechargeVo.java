package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author xiangyang
 * @date 2020/10/26
 */
@ApiModel(value = "充值卡充值统计返回")
@Data
public class RechargeVo {
	@ApiModelProperty(value = "优惠券id")
	private Integer couponId;
	@ApiModelProperty(value = "充值卡名称")
	private String couponName;
	@ApiModelProperty(value = "产品分类")
	private String couponCategoryName;
	@ApiModelProperty(value = "创建时间")
	private LocalDate crtDate;
	@ApiModelProperty(value = "本金/售出金额")
	private BigDecimal soldAmount;
	@ApiModelProperty(value = "产品创建人")
	private String crtUser;
	@ApiModelProperty(value = "已生成数量")
	private Long generatedQuantity;
	@ApiModelProperty(value = "已售出数量")
	private Long soldQuantity;
	@ApiModelProperty(value = "已充值数量")
	private Long rechargeQuantity;
}
