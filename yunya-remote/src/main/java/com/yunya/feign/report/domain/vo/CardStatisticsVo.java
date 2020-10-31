package com.yunya.feign.report.domain.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @author xiangyang
 * @date 2020/10/26
 */
@ApiModel(value = "产品售出激活统计详情（代金、折扣、兑换、套餐）返回")
@Data
public class CardStatisticsVo {
	@ApiModelProperty(value = "售出日期")
	@ExcelProperty("售出日期")
	private LocalDate soldDate;
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
	@ApiModelProperty(value = "激活日期")
	@ExcelProperty("激活日期")
	private LocalDate activeDate;
	@ApiModelProperty(value = "激活门诊")
	@ExcelProperty("激活门诊")
	private String activeOrgName;
	@ApiModelProperty(value = "激活人")
	@ExcelProperty("激活人")
	private String activeUser;
	@ApiModelProperty(value = "卡主")
	@ExcelProperty("卡主")
	private String cardOwner;
	@ApiModelProperty(value = "卡主手机号")
	@ExcelProperty("卡主手机号")
	private String cardOwnerPhoneNumber;
	@ApiModelProperty(value = "卡券使用有效截止日期")
	@ExcelProperty("卡券使用有效截止日期")
	private LocalDate activationDeadline;
	@ApiModelProperty(value = "是否已使用（0-否 1-是）")
	@ExcelProperty("是否已使用")
	private Integer used;
}
