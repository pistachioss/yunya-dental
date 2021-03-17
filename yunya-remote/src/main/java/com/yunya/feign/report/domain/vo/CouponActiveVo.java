package com.yunya.feign.report.domain.vo;

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
	@ApiModelProperty(value = "销售渠道")
	@ExcelProperty(value = "销售渠道")
	private String soldChannelName;
	@ApiModelProperty(value = "产品类型")
	private String couponCategoryName;
	@ApiModelProperty(value = "产品名称")
	private String couponName;
	@ApiModelProperty(value = "激活数量")
	private Long activatedQuantity;
}
