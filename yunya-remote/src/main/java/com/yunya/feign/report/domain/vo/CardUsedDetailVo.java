package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author chenl
 * @date 2021/01/18
 */
@ApiModel(value = "产品记录-产品使用详情返回")
@Data
@ToString
public class CardUsedDetailVo implements Serializable {
	@ApiModelProperty(value = "项目编号")
	private String itemNum;

	@ApiModelProperty(value = "项目")
	private String itemName;

	@ApiModelProperty(value = "数量")
	private Integer num;

	@ApiModelProperty(value = "执行人")
	private String executor;

	@ApiModelProperty(value = "优惠金额")
	private BigDecimal amount;
}
