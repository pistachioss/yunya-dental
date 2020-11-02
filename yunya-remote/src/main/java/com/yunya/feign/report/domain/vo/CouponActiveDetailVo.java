package com.yunya.feign.report.domain.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @author xiangyang
 * @date 2020/10/28
 */
@ApiModel(value = "第三方平台卡券激活明细返回")
@Data
public class CouponActiveDetailVo {
	@ApiModelProperty(value = "激活日期")
	@ExcelProperty("激活日期")
	private LocalDate activeDate;
	@ApiModelProperty(value = "激活门诊")
	@ExcelProperty("激活门诊")
	private String activeOrgName;
	@ApiModelProperty(value = "卡号")
	@ExcelProperty("卡号")
	private String cardNumber;
	@ApiModelProperty(value = "卡主")
	@ExcelProperty("卡主")
	private String cardOwner;
	@ApiModelProperty(value = "售出对象")
	@ExcelProperty("售出对象")
	private String soldTarget;
	@ApiModelProperty(value = "售出对象手机号")
	@ExcelProperty("售出对象手机号")
	private String soldPhoneNumber;
	@ApiModelProperty(value = "销售渠道")
	@ExcelProperty("销售渠道")
	private String soldChannelName;
	@ApiModelProperty(value = "激活人")
	@ExcelProperty("激活人")
	private String activeUser;
}
