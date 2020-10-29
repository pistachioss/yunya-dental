package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author xiangyang
 * @date 2020/10/27
 */
@ApiModel(value = "产品售出统计返回")
@Data
public class CouponSoldStatisticsVo {
	@ApiModelProperty(value = "优惠券id")
	private Integer couponId;
	@ApiModelProperty(value = "产品名称")
	private String couponName;
	@ApiModelProperty(value = "产品分类")
	private String couponCategoryName;
	@ApiModelProperty(value = "创建时间")
	private LocalDate crtDate;
	@ApiModelProperty(value = "产品类型")
	private Integer couponType;
	@ApiModelProperty(value = "售出金额")
	private BigDecimal soldAmount;
	@ApiModelProperty(value = "产品创建人")
	private String crtUser;
	@ApiModelProperty(value = "自有平台已售出数量")
	private Long ownPlatformSoldQuantity;
	@ApiModelProperty(value = "第三方平台已激活数量（充值卡为null）")
	private Long thirdPartyActivatedQuantity;
}
