package com.yunya.feign.report.domain.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author xiangyang
 * @date 2020/10/26
 */
@ApiModel(value = "充值卡充值统计-充值统计返回")
@Data
public class RechargeDetailVo {
	@ApiModelProperty(value = "卡号")
	@ExcelProperty(value = "卡号")
	private String cardNumber;
	@ApiModelProperty(value = "充值门诊")
	@ExcelProperty(value = "充值门诊")
	private String rechargeOrgName;
	@ApiModelProperty(value = "充值时间")
	@ExcelProperty(value = "充值时间")
	private String rechargeDate;
	@ApiModelProperty(value = "患者")
	@ExcelProperty(value = "患者")
	private String patientName;
	@ApiModelProperty(value = "患者手机号")
	@ExcelProperty(value = "患者手机号")
	private String patientMobile;
	@ApiModelProperty(value = "充值预付款账户")
	@ExcelProperty(value = "充值预付款账户")
	private String rechargeAccount;
	@ApiModelProperty(value = "充值本金")
	@ExcelProperty(value = "充值本金")
	private BigDecimal rechargeAmount;
	@ApiModelProperty(value = "充值赠金")
	@ExcelProperty(value = "充值赠金")
	private BigDecimal rechargeBonus;
	@ApiModelProperty(value = "充值人")
	@ExcelProperty(value = "充值人")
	private String rechargeUser;
}
