package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author xiangyang
 * @date 2020/11/16
 */
@Data
@ApiModel(value = "患者档案产品管理使用记录（兑换、套餐）返回")
public class MultiCardUseVo {
	@ApiModelProperty(value = "项目详情")
	private BenefitItemVo itemVo;
	@ApiModelProperty(value = "使用记录")
	private OnceCardUseVo useVo;
}
