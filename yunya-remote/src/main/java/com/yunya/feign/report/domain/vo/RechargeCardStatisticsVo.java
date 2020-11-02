package com.yunya.feign.report.domain.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author xiangyang
 * @date 2020/10/27
 */
@ApiModel(value = "产品售出激活统计详情（充值卡）返回")
@Data
public class RechargeCardStatisticsVo {
	@ApiModelProperty(value = "售出日期")
	@ExcelProperty("售出日期")
	private String soldDate;
	@ApiModelProperty(value = "卡号")
	@ExcelProperty("卡号")
	private String cardNumber;
	@ApiModelProperty(value = "分配对象")
	@ExcelProperty("分配对象")
	private String allocateOrgName;
	@ApiModelProperty(value = "售出对象")
	@ExcelProperty("售出对象")
	private String soldTarget;
	@ApiModelProperty(value = "售出对象手机号")
	@ExcelProperty("售出对象手机号")
	private String soldPhoneNumber;
	@ApiModelProperty(value = "售出类型（0-售出 1-置换 2-赠送）")
	@ExcelProperty("售出类型")
	private Integer soldType;
	@ApiModelProperty(value = "售出方式（0-线上 1-线下）")
	@ExcelProperty("售出方式")
	private Integer soldWay;
	@ApiModelProperty(value = "收费状态（0-否 1-是）")
	@ExcelProperty("收费状态")
	private Integer chargeStatus;
	@ApiModelProperty(value = "售出人")
	@ExcelProperty("售出人")
	private String soldUser;
	@ApiModelProperty(value = "充值日期")
	@ExcelProperty("充值日期")
	private String rechargeDate;
	@ApiModelProperty(value = "充值门诊")
	@ExcelProperty("充值门诊")
	private String rechargeOrgName;
	@ApiModelProperty(value = "充值人")
	@ExcelProperty("充值人")
	private String rechargeUser;
	@ApiModelProperty(value = "充值对象")
	@ExcelProperty("充值对象")
	private String rechargeTarget;
	@ApiModelProperty(value = "充值预付款账户")
	@ExcelProperty("充值预付款账户")
	private String rechargeAccount;
}
