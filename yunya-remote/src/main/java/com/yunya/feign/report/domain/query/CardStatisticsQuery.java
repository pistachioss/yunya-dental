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
@ApiModel(value = "产品售出激活统计详情（代金、折扣、兑换、套餐）参数")
@Data
public class CardStatisticsQuery extends PageQuery {
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
	@ApiModelProperty(value = "激活门诊")
	private List<Integer> activeOrgIds;
	@ApiModelProperty(value = "激活开始日期")
	private LocalDate activeStartDate;
	@ApiModelProperty(value = "激活结束日期")
	private LocalDate activeEndDate;
	@ApiModelProperty(value = "线上/线下（0-线上 1-线下 2-小程序订单）")
	private List<Integer> soldWays;
	@ApiModelProperty(value = "收费状态（0-否 1-是）")
	private List<Integer> chargeStatus;
	@ApiModelProperty(value = "备注 后端使用")
	private String remark;
}
