package com.yunya.middletable.model.credits_shop;

import lombok.Data;

import java.util.Date;

/**
 * @program: yunya-dental
 * @description: 积分通知参数封装
 * @author: LHB
 * @create: 2021-04-22 09:36
 **/
@Data
public class CreditNotifyParams {

	private boolean success;
	private String bizId="";
	private String errorMessage="";
	private String orderNum="";
	private Date timestamp=new Date();
	private String appKey;
	private String uid="";
	private String transfer="";
}
