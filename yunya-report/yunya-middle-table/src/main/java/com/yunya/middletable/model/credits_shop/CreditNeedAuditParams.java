package com.yunya.middletable.model.credits_shop;

import com.yunya.middletable.utils.SignTool;
import lombok.Data;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: yunya-dental
 * @description: 积分审核参数
 * @author: LHB
 * @create: 2021-04-22 09:36
 **/
@Data
public class CreditNeedAuditParams extends AbstractCredit {

	private String appKey;
	private String bizId="";
	private Date timestamp;
	
	public CreditNeedAuditParams(){
	}
	
	@Override
	public Map<String, String> toRequestMap(String appSecret, String signUrl){
		Map<String, String> map=new HashMap<String, String>();
		map.put("bizId", bizId+"");
		map.put("appKey", appKey+"");
		map.put("appSecret", appSecret+"");
		map.put("timestamp", timestamp.getTime()+"");
		
		map.remove("appSecret");
		map.put("sign", signUrl);
		return map;
	}
}
