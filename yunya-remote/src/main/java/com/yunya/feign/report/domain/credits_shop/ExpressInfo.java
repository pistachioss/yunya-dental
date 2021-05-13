package com.yunya.feign.report.domain.credits_shop;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: yunya-dental
 * @description: 构建开发者批量发货的发货信息
 * @author: LHB
 * @create: 2021-04-22 09:36
 **/
@Data
public class ExpressInfo implements Serializable {
	@ApiModelProperty("兑吧订单号")
	private String orderNum;
	@ApiModelProperty("快递单号")
	private String expressNum;
	@ApiModelProperty("快递类型")
	private String expressType;
	
	/**
	 * 构建发货信息的toString方法
	 */
	@Override
	public String toString() {
		return orderNum + "|"+ expressType + "|" + expressNum + ",";
	}
	
	
}
