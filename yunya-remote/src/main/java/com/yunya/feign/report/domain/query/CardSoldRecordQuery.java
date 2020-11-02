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
@ApiModel(value = "产品记录-产品售出记录参数")
@Data
public class CardSoldRecordQuery extends PageQuery {
	@ApiModelProperty(value = "门诊")
	private Integer orgId;
	@ApiModelProperty(value = "售出开始日期")
	private LocalDate soldStartDate;
	@ApiModelProperty(value = "售出结束日期")
	private LocalDate soldEndDate;
	@ApiModelProperty(value = "产品名称")
	private String couponName;
	@ApiModelProperty(value = "卡号")
	private String cardNumber;
	@ApiModelProperty(value = "售出对象")
	private String soldTarget;
	@ApiModelProperty(value = "售出对象手机号")
	private String soldPhoneNumber;
	@ApiModelProperty(value = "产品类型（0-代金券；1-折扣券；2-兑换券；3-套餐券；4-充值券）")
	private List<Integer> couponTypes;
}
