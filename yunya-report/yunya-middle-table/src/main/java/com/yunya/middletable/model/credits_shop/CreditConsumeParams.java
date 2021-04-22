package com.yunya.middletable.model.credits_shop;

import com.yunya.middletable.utils.SignTool;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: yunya-dental
 * @description: 积分消费请求参数
 * @author: LHB
 * @create: 2021-04-22 09:36
 **/
@ApiModel(value = "CreditConsumeParams",description = "积分消费请求参数")
@Data
public class CreditConsumeParams extends AbstractCredit {

	private String appKey;
	@ApiModelProperty("时间戳")
	private Date timestamp;
	@ApiModelProperty("消耗积分数")
	private Long credits;
	@ApiModelProperty("兑吧订单号")
	private String orderNum="";
	@ApiModelProperty("描述")
	private String description="";
	@ApiModelProperty("商品编码，非必须参数")
	private String itemCode="";
	@ApiModelProperty("类型：QB,Phonebill,Alipay,Coupon  所有类型不区分大小写")
	private String type="";
	@ApiModelProperty("面值，分为单位")
	private Integer facePrice=0;
	@ApiModelProperty("实际扣款，分为单位")
	private Integer actualPrice=0;
	@ApiModelProperty("用户唯一标识")
	private String uid="";
	@ApiModelProperty("是否等待审核， 如果返回true，表示此订单需要审核，审核通过后才会继续下去。 如果返回false表示此订单无须审核，会直接继续兑换流程")
	private boolean waitAudit=false;
	@ApiModelProperty("用户兑换时使用的ip地址，有可能为空")
	private String ip="";
	@ApiModelProperty("参数，根据不同的type，有不同的含义，参见在线文档")
	private String params="";
	@ApiModelProperty("自定义参数")
	private String transfer="";
	
	@Override
	public Map<String, String> toRequestMap(String appSecret, String signUrl){
		Map<String, String> map=new HashMap<String, String>();
		map.put("credits", credits+"");
		map.put("description", description);
		map.put("uid", uid);
		map.put("appKey", appKey);
		map.put("waitAudit", waitAudit+"");
		map.put("appSecret", appSecret);
		map.put("timestamp", System.currentTimeMillis()+"");
		map.put("orderNum", orderNum);
		map.put("type", type);
		map.put("facePrice", facePrice+"");
		map.put("actualPrice", actualPrice+"");
		map.put("ip", ip);
		map.put("params", params);
		putIfNotEmpty(map, "itemCode", itemCode);
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
