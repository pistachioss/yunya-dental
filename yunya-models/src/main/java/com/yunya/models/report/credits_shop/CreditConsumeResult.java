package com.yunya.models.report.credits_shop;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: yunya-dental
 * @description: 积分消费结果
 * @author: LHB
 * @create: 2021-04-22 09:36
 **/
@Data
@ApiModel(value = "CreditConsumeResult",description = "积分消费结果")
public class CreditConsumeResult implements Serializable {

	private boolean success;
	private String errorMessage="";
	private String bizId="";
	@ApiModelProperty("用户积分余额")
	private Long credits=-1L;
	
	public CreditConsumeResult(boolean success){
		this.success=success;
	}
	
	
	@Override
	public String toString(){
		if(success){
			return "{'status':'ok','errorMessage':'','bizId':'"+bizId+"','credits':'"+credits+"'}";
		}else{
			return "{'status':'fail','errorMessage':'"+errorMessage+"','credits':'"+credits+"'}";
		}
	}
}
