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
@ApiModel(value = "第三方平台卡券激活明细参数")
@Data
public class CouponActiveDetailQuery extends PageQuery {
	@ApiModelProperty(value = "卡号")
	private String cardNumber;
	@ApiModelProperty(value = "售出对象")
	private String soldTarget;
	@ApiModelProperty(value = "销售渠道")
	private List<Integer> soldChannelIds;
	@ApiModelProperty(value = "激活开始日期")
	private LocalDate activeStartDate;
	@ApiModelProperty(value = "激活结束日期")
	private LocalDate activeEndDate;
	@ApiModelProperty(value = "激活门诊")
	private List<Integer> activeOrgIds;
}
