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
 * @date 2020/10/28
 */
@ApiModel(value = "产品优惠项目明细参数")
@Data
public class CardBenefitItemQuery extends PageQuery {
	@ApiModelProperty(value = "门诊")
	@NotNull(message = "请选择门诊")
	private Integer orgId;
	@ApiModelProperty(value = "开单开始日期")
	@NotNull(message = "请选择使用开始日期")
	private LocalDate usedStartDate;
	@ApiModelProperty(value = "开单结束日期")
	@NotNull(message = "请选择使用结束日期")
	private LocalDate usedEndDate;
	@ApiModelProperty(value = "患者")
	private String patientKeyword;
	@ApiModelProperty(value = "产品名称")
	private String couponName;
	@ApiModelProperty(value = "卡号")
	private String cardNumber;
	@ApiModelProperty(value = "项目分类")
	private List<Integer> itemCategoryIds;
}
