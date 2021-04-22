package com.yunya.middletable.model.credits_shop;

import com.yunya.middletable.utils.SignTool;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: yunya-dental
 * @description: 新增积分请求参数分装
 * @author: LHB
 * @create: 2021-04-22 09:36
 **/
@Data
@ApiModel(value = "AddCreditsParams",description = "新增积分请求参数分装")
public class AddCreditsParams extends AbstractCredit implements Serializable {

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

	@Override
	public Map<String, String> toRequestMap(String appSecret, String signUrl){
		Map<String, String> map=new HashMap<String, String>();
		map.put("credits", credits+"");
		map.put("description", description);
		map.put("uid", uid);
		map.put("appKey", appKey);
		map.put("appSecret", appSecret);
		map.put("timestamp",  System.currentTimeMillis()+"");
		map.put("orderNum", orderNum);
		map.put("type", type);
		map.put("ip", ip);
		putIfNotEmpty(map, "transfer", transfer);
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
}
