package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author xiangyang
 * @date 2020/11/16
 */
@Data
public class BenefitItemVo {
	@ApiModelProperty("开单日期")
	private Integer itemId;
	@ApiModelProperty("项目名称")
	private Integer itemName;
	@ApiModelProperty("数量")
	private Integer originalQuantity;
	@ApiModelProperty("剩余可使用数量")
	private Integer remainingQuantity;
}
