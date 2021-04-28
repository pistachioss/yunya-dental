package com.yunya.models.report.credits_shop;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @program: yunya-dental
 * @description: 积分审核参数
 * @author: LHB
 * @create: 2021-04-22 09:36
 **/
@Data
public class CreditNeedAuditParams implements Serializable {

	private String appKey;
	private String bizId="";
	private Date timestamp;
}
