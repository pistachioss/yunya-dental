package com.yunya.feign.report.domain.query;

import com.yunya.framework.common.model.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

/**
 * @author xiangyang
 * @date 2020/10/27
 */
@ApiModel(value = "产品售出激活统计详情（充值卡）参数")
@Data
public class RechargeCardStatisticsQuery extends PageQuery {
	@ApiModelProperty(value = "卡号")
	private String cardNumber;
	@ApiModelProperty(value = "分配对象")
	private List<Integer> allocateOrgIds;
	@ApiModelProperty(value = "售出类型（0-售出 1-置换 2-赠送）")
	private List<Integer> soldTypes;
	@ApiModelProperty(value = "售出开始日期")
	private LocalDate soldStartDate;
	@ApiModelProperty(value = "售出结束日期")
	private LocalDate soldEndDate;
	@ApiModelProperty(value = "充值门诊")
	private List<Integer> rechargeOrgIds;
	@ApiModelProperty(value = "充值开始日期")
	private LocalDate rechargeStartDate;
	@ApiModelProperty(value = "充值结束日期")
	private LocalDate rechargeEndDate;
	@ApiModelProperty(value = "线上/线下（0-线上 1-线下）")
	private List<Integer> soldWays;
}
