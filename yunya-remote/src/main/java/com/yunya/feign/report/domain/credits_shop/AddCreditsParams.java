package com.yunya.feign.report.domain.credits_shop;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @program: yunya-dental
 * @description: 新增积分请求参数分装
 * @author: LHB
 * @create: 2021-04-22 09:36
 **/
@Data
@ApiModel(value = "AddCreditsParams",description = "新增积分请求参数分装")
public class AddCreditsParams implements Serializable {

	private String appKey;
	@ApiModelProperty("时间戳")
	private Date timestamp;
	@ApiModelProperty("增加积分值")
	private Long credits;
	@ApiModelProperty("兑吧订单号")
	private String orderNum="";
	@ApiModelProperty("新增积分描述")
	private String description="";
	@ApiModelProperty("积分活类型，活动和签到")
	private String type="";
	@ApiModelProperty("用户唯一id")
	private String uid="";
	@ApiModelProperty("用户兑换时使用的ip地址，有可能为空")
	private String ip="";
	@ApiModelProperty("非必须参数")
	private String transfer="";
}
