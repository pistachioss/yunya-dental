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
@ApiModel(value = "产品使用统计-时间维度返回")
@Data
public class CouponUsedDetailVo {
	@ApiModelProperty(value = "卡号")
	private String cardNumber;
	@ApiModelProperty(value = "卡主")
	private String cardOwner;
	@ApiModelProperty(value = "销售渠道")
	private String saleChannelName;
	@ApiModelProperty(value = "使用门诊")
	private String usedClinicName;
	@ApiModelProperty(value = "使用时间")
	private LocalDate usedDate;
	@ApiModelProperty(value = "使用患者")
	private String usedPatientName;
	@ApiModelProperty(value = "患者手机号")
	private String usedPatientMobile;
	@ApiModelProperty(value = "挂号医生")
	private String dentistName;
	@ApiModelProperty(value = "账单编号")
	private String billNumber;
	@ApiModelProperty(value = "账单原价合计")
	private BigDecimal orderAmount;
	@ApiModelProperty(value = "卡券优惠金额")
	private BigDecimal benefitAmount;
	@ApiModelProperty(value = "账单实收")
	private BigDecimal actualAmount;
	@ApiModelProperty(value = "优惠操作人")
	private String operateBenefitUser;
}
