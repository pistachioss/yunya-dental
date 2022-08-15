package com.yunya.feign.ivy_mini.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @description:
 * @author: xy
 * @date 2022/5/13 13:15
 **/
@Data
@ApiModel(description = "创建产品订单参数（购物车）")
public class CreateCartOrderModel extends CreateOrderBaseModel{
    @ApiModelProperty(value = "购物车id集合", required = true)
    @NotEmpty
    private List<Integer> cartIds;
    @ApiModelProperty(value = "收货地址ID", required = true)
    @NotNull
    private Integer fansReceiveAddressId;
}
