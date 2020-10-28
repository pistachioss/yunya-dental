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
@ApiModel(value = "产品使用统计参数")
@Data
public class CouponUsedQuery extends PageQuery {
	@ApiModelProperty(value = "产品名称")
	private String couponName;
	@ApiModelProperty(value = "产品分类")
	private List<Integer> couponCategoryIds;
	@ApiModelProperty(value = "产品类型（0-代金券；1-折扣券；2-兑换券；3-套餐券；4-充值券）")
	private List<Integer> couponTypes;
}
