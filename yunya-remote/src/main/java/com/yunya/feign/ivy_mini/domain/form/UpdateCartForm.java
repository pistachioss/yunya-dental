package com.yunya.feign.ivy_mini.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description:
 * @author: xy
 * @date 2022/5/13 13:15
 **/
@Data
@ApiModel(description = "添加购物车参数")
public class UpdateCartForm {
    @ApiModelProperty(value = "id", required = true)
    @NotNull
    private Integer id;
    @ApiModelProperty(value = "修改后数量", required = true)
    @NotNull
    private Integer quantity;
}
