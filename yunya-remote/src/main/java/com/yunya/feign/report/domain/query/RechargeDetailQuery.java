package com.yunya.feign.report.domain.query;

import com.yunya.framework.common.model.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * @author xiangyang
 * @date 2020/10/26
 */
@ApiModel(value = "充值卡充值统计-充值统计参数")
@Data
public class RechargeDetailQuery extends PageQuery {
	@ApiModelProperty(value = "卡号")
	private String cardNumber;
	@ApiModelProperty(value = "患者")
	private String patientKeyWord;
	@ApiModelProperty(value = "充值门诊")
	private List<Integer> rechargeOrgIds;
	@ApiModelProperty(value = "充值预付款账户")
	private String rechargeAccount;
	@ApiModelProperty(value = "充值开始时间")
	private LocalDate rechargeStartDate;
	@ApiModelProperty(value = "充值结束时间")
	private LocalDate rechargeEndDate;
}
