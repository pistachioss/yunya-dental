package com.yunya.feign.ivy_mini.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 简介:
 *
 * @author: ylx
 * @date: 2022/6/23
 * @description:
 */
@Data
@ApiModel(value = "意见反馈")
public class FeedBackAddForm {
    @ApiModelProperty(value = "意见内容")
    private String context;
}
