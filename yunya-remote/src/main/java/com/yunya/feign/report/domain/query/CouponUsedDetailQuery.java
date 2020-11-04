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
@ApiModel(value = "产品使用统计-产品维度-使用统计参数")
@Data
public class CouponUsedDetailQuery extends PageQuery {
	@ApiModelProperty(value = "卡号")
	private String cardNumber;
	@ApiModelProperty(value = "使用患者")
	private String patientKeyWord;
	@ApiModelProperty(value = "账单编号")
	private String billNumber;
	@ApiModelProperty(value = "使用门诊")
	private List<Integer> orgIds;
	@ApiModelProperty(value = "挂号医生")
	private List<Integer> dentistIds;
	@ApiModelProperty(value = "使用开始时间")
	private LocalDate usedStartDate;
	@ApiModelProperty(value = "使用结束时间")
	private LocalDate usedEndDate;
	@ApiModelProperty(value = "销售渠道")
	private List<Integer> soldChannelIds;
}
