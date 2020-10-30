package com.yunya.feign.report.domain.query;

import com.yunya.framework.common.model.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author xiangyang
 * @date 2020/10/26
 */
@ApiModel(value = "充值卡充值统计参数")
@Data
public class RechargeQuery extends PageQuery {
	@ApiModelProperty(value = "充值卡名称")
	private String couponName;
	@ApiModelProperty(value = "产品分类")
	private List<Integer> couponCategoryIds;
}
