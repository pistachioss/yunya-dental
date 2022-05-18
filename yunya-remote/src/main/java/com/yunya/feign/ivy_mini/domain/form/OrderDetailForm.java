package com.yunya.feign.ivy_mini.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 简介:
 *
 * @author: ylx
 * @date: 2022/5/18
 * @description:
 */
@Data
@ApiModel(value = "订单详情")
public class OrderDetailForm {

    private Integer id;
    @ApiModelProperty("openID")
    private String openId;

}
