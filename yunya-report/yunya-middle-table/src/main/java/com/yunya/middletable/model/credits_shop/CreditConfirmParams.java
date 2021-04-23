package com.yunya.middletable.model.credits_shop;

import io.swagger.annotations.ApiModel;
import lombok.Data;
/**
 * @program: yunya-dental
 * @description: 积分兑换确认请求参数
 * 开发者向兑吧发起请求，确认兑换成功失败的参数
 * 目前仅限于虚拟商品，需要使用
 * @author: LHB
 * @create: 2021-04-22 09:36
 **/
@Data
@ApiModel(value = "CreditConfirmParams",description = "积分兑换确认请求参数")
public class CreditConfirmParams {
	private boolean success=true;
	private String errorMessage="";
	private String orderNum="";
}
