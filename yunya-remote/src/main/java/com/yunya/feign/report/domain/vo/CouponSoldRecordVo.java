package com.yunya.feign.report.domain.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author xiangyang
 * @date 2020/10/28
 */
@ApiModel(value = "产品记录-产品售出记录返回")
@Data
public class CouponSoldRecordVo {
	@ApiModelProperty(value = "售出日期")
	@ExcelProperty("售出日期")
	private String soldDate;
	@ApiModelProperty(value = "售出对象")
	@ExcelProperty("售出对象")
	private String soldTarget;
	@ApiModelProperty(value = "售出对象手机号")
	@ExcelProperty("售出对象手机号")
	private String soldPhoneNumber;
	@ApiModelProperty(value = "产品名称")
	@ExcelProperty("产品名称")
	private String couponName;
	@ApiModelProperty(value = "产品类型")
	@ExcelProperty("产品类型")
	private String couponType;
	@ApiModelProperty(value = "卡号")
	@ExcelProperty("卡号")
	private String cardNumber;
	@ApiModelProperty(value = "售出金额")
	@ExcelProperty("售出金额")
	private BigDecimal soldAmount;
	@ApiModelProperty(value = "售出类型（0-售出 1-置换 2-赠送）")
	@ExcelProperty("售出类型")
	private String soldType;
	@ApiModelProperty(value = "收费状态（0-否 1-是）")
	@ExcelProperty("收费状态")
	private String chargeStatus;
	@ApiModelProperty(value = "入账方式")
	@ExcelProperty("入账方式")
	private String payName;
	@ApiModelProperty(value = "售出人")
	@ExcelProperty("售出人")
	private String sellerUserName;
}
