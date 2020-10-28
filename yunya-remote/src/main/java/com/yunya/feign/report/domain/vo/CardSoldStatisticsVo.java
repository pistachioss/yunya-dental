package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author xiangyang
 * @date 2020/10/28
 */
@ApiModel(value = "产品售出统计-时间维度返回")
@Data
public class CardSoldStatisticsVo {
	@ApiModelProperty(value = "售出日期")
	private LocalDate soldDate;
	@ApiModelProperty(value = "产品名称")
	private String couponName;
	@ApiModelProperty(value = "售出金额")
	private BigDecimal soldAmount;
	@ApiModelProperty(value = "卡号")
	private String cardNumber;
	@ApiModelProperty(value = "分配对象")
	private String allocateOrgName;
	@ApiModelProperty(value = "售出对象")
	private String soldTarget;
	@ApiModelProperty(value = "售出对象手机号")
	private String soldPhoneNumber;
	@ApiModelProperty(value = "售出类型（0-售出 1-置换 2-赠送）")
	private Integer soldType;
	@ApiModelProperty(value = "售出方式（0-线上 1-线下）")
	private Integer soldWay;
	@ApiModelProperty(value = "收费状态（0-否 1-是）")
	private Integer chargeStatus;
	@ApiModelProperty(value = "入账方式")
	private String payName;
	@ApiModelProperty(value = "售出人")
	private String soldUser;
}
