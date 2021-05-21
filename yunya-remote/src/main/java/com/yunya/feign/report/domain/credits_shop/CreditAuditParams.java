package com.yunya.feign.report.domain.credits_shop;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
/**
 * @program: yunya-dental
 * @description: 订单审核请求参数封装
 * 开发者向兑吧发起请求，通知兑吧，订单的审核结果的参数
 * 兑吧产生的部分订单，出于安全的考虑，会要求开发者进行审核，审核通过后才会继续执行兑换操作
 * 如果开发者要告知兑吧审核结果，可以向兑吧发起请求，构建这个 CreditAuditParams对象，将此对象发送给兑吧即可
 * @author: LHB
 * @create: 2021-04-22 09:36
 **/
@Data
@ApiModel(value = "CreditAuditParams",description = "订单审核请求参数封装")
public class CreditAuditParams implements Serializable {

	@ApiModelProperty("审核通过的订单列表")
	private List<String> passOrderNums=new ArrayList<String>();
	@ApiModelProperty("审核不通过的订单列表")
	private List<String> rejectOrderNums=new ArrayList<String>();
}
