package com.yunya.feign.report.domain.vo;

import com.alibaba.excel.annotation.ExcelProperty;
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
public class CardUsedStatisticsVo {
	@ApiModelProperty(value = "产品名称")
	@ExcelProperty(value = "产品名称")
	private String couponName;
	@ApiModelProperty(value = "产品类型（0-代金券；1-折扣券；2-兑换券；3-套餐券；4-充值券）")
	@ExcelProperty(value = "产品类型")
	private Integer couponType;
	@ApiModelProperty(value = "卡号")
	@ExcelProperty(value = "卡号")
	private String cardNumber;
	@ApiModelProperty(value = "卡主")
	@ExcelProperty(value = "卡主")
	private String cardOwner;
	@ApiModelProperty(value = "销售渠道")
	@ExcelProperty(value = "销售渠道")
	private String saleChannelName;
	@ApiModelProperty(value = "使用门诊")
	@ExcelProperty(value = "使用门诊")
	private String usedClinicName;
	@ApiModelProperty(value = "使用时间")
	@ExcelProperty(value = "使用时间")
	private LocalDate usedDate;
	@ApiModelProperty(value = "使用患者")
	@ExcelProperty(value = "使用患者")
	private String usedPatientName;
	@ApiModelProperty(value = "患者手机号")
	@ExcelProperty(value = "患者手机号")
	private String usedPatientMobile;
	@ApiModelProperty(value = "挂号医生")
	@ExcelProperty(value = "挂号医生")
	private String dentistName;
	@ApiModelProperty(value = "账单编号")
	@ExcelProperty(value = "账单编号")
	private String billNumber;
	@ApiModelProperty(value = "账单原价合计")
	@ExcelProperty(value = "账单原价合计")
	private BigDecimal orderAmount;
	@ApiModelProperty(value = "卡券优惠金额")
	@ExcelProperty(value = "卡券优惠金额")
	private BigDecimal benefitAmount;
	@ApiModelProperty(value = "账单实收")
	@ExcelProperty(value = "账单实收")
	private BigDecimal actualAmount;
	@ApiModelProperty(value = "优惠操作人")
	@ExcelProperty(value = "优惠操作人")
	private String operateBenefitUser;
}
