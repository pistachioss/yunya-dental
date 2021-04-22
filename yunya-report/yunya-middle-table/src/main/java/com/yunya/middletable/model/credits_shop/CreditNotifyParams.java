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

	/** 兑换是否成功，状态是true和false */
	private boolean success;
	/** 开发者的订单号 */
	private String bizId="";
	/** 出错原因(带中文，请用utf-8进行解码) */
	private String errorMessage="";
	/** 兑吧订单号 */
	private String orderNum="";
	/** 1970-01-01开始的时间戳，毫秒 */
	private Date timestamp=new Date();
	/** 接口appKey，应用的唯一标识码 */
	private String appKey;
	/** 用户唯一标识，唯一且不可变  openId + "#" + patientId */
	private String uid="";
	/** 自定义参数 */
	private String transfer="";
}
