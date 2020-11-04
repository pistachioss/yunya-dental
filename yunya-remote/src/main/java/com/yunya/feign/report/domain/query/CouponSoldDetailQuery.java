package com.yunya.feign.report.domain.query;

import com.yunya.framework.common.model.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * @author xiangyang
 * @date 2020/10/28
 */
@ApiModel(value = "自有平台卡券售出明细参数")
@Data
public class CouponSoldDetailQuery extends PageQuery {
	@ApiModelProperty(value = "卡号")
	private String cardNumber;
	@ApiModelProperty(value = "售出对象")
	private String soldTarget;
	@ApiModelProperty(value = "分配对象")
	private List<Integer> allocateOrgIds;
	@ApiModelProperty(value = "售出类型（0-售出 1-置换 2-赠送）")
	private List<Integer> soldTypes;
	@ApiModelProperty(value = "售出开始日期")
	private LocalDate soldStartDate;
	@ApiModelProperty(value = "售出结束日期")
	private LocalDate soldEndDate;
	@ApiModelProperty(value = "线上/线下（0-线上 1-线下）")
	private List<Integer> soldWays;
	@ApiModelProperty(value = "收费状态（0-否 1-是）")
	private List<Integer> chargeStatus;
}
