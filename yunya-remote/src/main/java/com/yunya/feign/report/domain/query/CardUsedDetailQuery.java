package com.yunya.feign.report.domain.query;

import com.yunya.framework.common.model.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author chenl
 * @date 2021/01/19
 */
@ApiModel(value = "产品记录-产品使用详情参数")
@Data
public class CardUsedDetailQuery extends PageQuery {
	@ApiModelProperty(value = "账单ID")
	@NotNull(message = "账单ID不能为空")
	private Integer orderId;

	@ApiModelProperty(value = "卡券ID")
	@NotNull(message = "卡券ID不能为空")
	private Integer cardId;
}
