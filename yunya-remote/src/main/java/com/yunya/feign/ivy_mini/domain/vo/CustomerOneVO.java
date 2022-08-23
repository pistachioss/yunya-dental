package com.yunya.feign.ivy_mini.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 简介:
 *
 * @author: ylx
 * @date: 2022/6/22
 * @description:
 */
@Data
@ApiModel(description = "客服链接")
public class CustomerOneVO {
    @ApiModelProperty("客服链接")
    private String url;
}
