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
@ApiModel(value = "产品优惠项目明细返回")
@Data
public class CardBenefitItemVo {
	@ApiModelProperty(value = "项目编号")
	@ExcelProperty(value = "项目编号")
	private String itemNum;
	@ApiModelProperty(value = "项目名称")
	@ExcelProperty(value = "项目名称")
	private String itemName;
	@ApiModelProperty(value = "项目分类")
	@ExcelProperty(value = "项目分类")
	private String itemCategoryName;
	@ApiModelProperty(value = "单价")
	@ExcelProperty(value = "单价")
	private BigDecimal unit;
	@ApiModelProperty(value = "数量")
	@ExcelProperty(value = "数量")
	private Integer quantity;
	@ApiModelProperty(value = "原价合计")
	@ExcelProperty(value = "原价合计")
	private BigDecimal totalPrice;
	@ApiModelProperty(value = "优惠金额")
	@ExcelProperty(value = "优惠金额")
	private BigDecimal benefitAmount;
	@ApiModelProperty(value = "账单编号")
	@ExcelProperty(value = "账单编号")
	private String billNumber;
	@ApiModelProperty(value = "开单日期")
	@ExcelProperty(value = "开单日期")
	private String orderDate;
	@ApiModelProperty(value = "患者")
	@ExcelProperty(value = "患者")
	private String patientName;
	@ApiModelProperty(value = "手机号")
	@ExcelProperty(value = "手机号")
	private String patientMobile;
	@ApiModelProperty(value = "使用产品")
	@ExcelProperty(value = "使用产品")
	private String couponName;
	@ApiModelProperty(value = "产品类型（0-代金券；1-折扣券；2-兑换券；3-套餐券；4-充值券）")
	@ExcelProperty(value = "产品类型")
	private String couponType;
	@ApiModelProperty(value = "卡号")
	@ExcelProperty(value = "卡号")
	private String cardNumber;
	@ApiModelProperty(value = "销售渠道")
	@ExcelProperty(value = "销售渠道")
	private String saleChannelName;
}
