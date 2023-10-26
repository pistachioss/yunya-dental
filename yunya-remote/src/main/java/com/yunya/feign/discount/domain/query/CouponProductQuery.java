package com.yunya.feign.discount.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author xiangyang
 * @date 2023/7/17
 */
@Data
@ApiModel(value = "划扣列表查询参数")
public class CouponProductQuery {
    @ApiModelProperty(value = "分类id", required = true)
    @NotNull
    private Integer categoryId;
    @ApiModelProperty(value = "会员类型id", required = true)
    @NotNull
    private Integer memberTypeId;
}
