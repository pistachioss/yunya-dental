package com.yunya.feign.ivy_mini.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: xy
 * @date 2022/5/13 13:15
 **/
@Data
@ApiModel(description = "添加购物车参数")
public class UpdateCartForm {
    @ApiModelProperty(value = "产品id", required = true)
    private Integer productId;
    @ApiModelProperty(value = "数量", required = true)
    private Integer quantity;
}
