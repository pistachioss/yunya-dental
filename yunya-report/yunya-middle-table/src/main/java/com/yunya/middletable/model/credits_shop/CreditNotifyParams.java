package com.yunya.middletable.model.credits_shop;

import com.yunya.middletable.utils.SignTool;
import lombok.Data;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: yunya-dental
 * @description: 积分通知参数封装
 * @author: LHB
 * @create: 2021-04-22 09:36
 **/
@Data
public class CreditNotifyParams extends AbstractCredit {

	private boolean success;
	private String bizId="";
	private String errorMessage="";
	private String orderNum="";
	private Date timestamp=new Date();
	private String appKey;
	private String uid="";
	private String transfer="";
	
	@Override
	public Map<String, String> toRequestMap(String appSecret, String signUrl){
		Map<String, String> map=new HashMap<String, String>();
		map.put("success", success+"");
		map.put("errorMessage", getString(errorMessage));
		map.put("bizId", getString(bizId));
		map.put("appKey", getString(appKey));
		map.put("appSecret", getString(appSecret));
		putIfNotEmpty(map, "transfer", transfer);
		map.put("timestamp",getString( timestamp.getTime()));
		map.put("uid", getString(uid));
		map.put("orderNum", getString(orderNum));
		map.remove("appSecret");
		map.put("sign", signUrl);
		return map;
	}
	@Override
	void putIfNotEmpty(Map<String, String> map, String key, String value){
		if(value==null || value.length()==0){
			return;
		}
		map.put(key, value);
	}
	
	@Override
	String getString(Object o){
		if(o==null){
			return "";
		}
		return o.toString();
	}
}
