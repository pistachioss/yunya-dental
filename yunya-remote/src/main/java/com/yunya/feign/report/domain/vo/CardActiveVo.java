package com.yunya.feign.report.domain.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author xiangyang
 * @date 2020/10/26
 */
@ApiModel(value = "公司端/门诊端-产品激活明细返回")
@Data
public class CardActiveVo {
	@ApiModelProperty(value = "患者")
	@ExcelProperty("患者")
	private String patientName;
	@ExcelProperty("初诊日期")
	@ApiModelProperty("初诊日期")
	private String firstTreatDate;
	@ApiModelProperty(value = "激活时间")
	@ExcelProperty("激活时间")
	private String activeDate;
	@ApiModelProperty(value = "卡号")
	@ExcelProperty("卡号")
	private String cardNumber;
	@ApiModelProperty(value = "激活门诊")
	@ExcelProperty("激活门诊")
	private String activeOrgName;
	@ApiModelProperty(value = "操作人")
	@ExcelProperty("操作人")
	private String operateUser;
	@ApiModelProperty(value = "是否已使用（0-否 1-是）")
	@ExcelProperty("是否已使用")
	private String used;
}
