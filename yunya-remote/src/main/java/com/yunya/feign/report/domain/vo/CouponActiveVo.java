package com.yunya.feign.report.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author xiangyang
 * @date 2020/10/28
 */
@ApiModel(value = "公司端/门诊端-产品激活报表返回")
@Data
public class CouponActiveVo {
	@ApiModelProperty(value = "产品id")
	@ExcelIgnore
	private Integer couponId;
	@ApiModelProperty(value = "渠道id")
	@ExcelIgnore
	private Integer saleChannelId;
	@ApiModelProperty(value = "销售渠道")
	@ExcelProperty(value = "销售渠道")
	private String saleChannelName;
	@ApiModelProperty(value = "产品类型")
	@ExcelProperty(value = "产品类型")
	private String couponTypeName;
	@ApiModelProperty(value = "产品名称")
	@ExcelProperty(value = "产品名称")
	private String couponName;
	@ApiModelProperty(value = "激活门诊ID")
	@ExcelIgnore
	private String orgId;
	@ApiModelProperty(value = "激活门诊")
	@ExcelProperty(value = "激活门诊")
	private String orgName;
	@ApiModelProperty(value = "激活数量")
	@ExcelProperty(value = "激活数量")
	private Long activatedQuantity;
}
