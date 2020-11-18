package com.yunya.feign.report.domain.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author xiangyang
 * @date 2020/10/28
 */
@ApiModel(value = "产品记录-产品使用记录返回")
@Data
public class CardUsedRecordVo {
	@ApiModelProperty(value = "产品id")
	@ExcelProperty(value = "产品id")
	private Integer couponId;
	@ApiModelProperty(value = "订单id")
	@ExcelProperty(value = "订单id")
	private Integer orderId;
	@ApiModelProperty(value = "使用时间")
	@ExcelProperty(value = "使用时间")
	private String usedDate;
	@ApiModelProperty(value = "开单时间")
	@ExcelProperty(value = "开单时间")
	private String orderDate;
	@ApiModelProperty(value = "账单编号")
	@ExcelProperty(value = "账单编号")
	private String billNumber;
	@ApiModelProperty(value = "患者姓名")
	@ExcelProperty(value = "患者姓名")
	private String usedPatientName;
	@ApiModelProperty(value = "手机号")
	@ExcelProperty(value = "手机号")
	private String usedPatientMobile;
	@ApiModelProperty(value = "产品名称")
	@ExcelProperty(value = "产品名称")
	private String couponName;
	@ApiModelProperty(value = "产品类型（0-代金券；1-折扣券；2-兑换券；3-套餐券；4-充值券）")
	@ExcelProperty(value = "产品类型")
	private String couponType;
	@ApiModelProperty(value = "销售渠道")
	@ExcelProperty(value = "销售渠道")
	private String saleChannelName;
	@ApiModelProperty(value = "卡号")
	@ExcelProperty(value = "卡号")
	private String cardNumber;
	@ApiModelProperty(value = "收费人")
	private String operateBenefitUser;
}
