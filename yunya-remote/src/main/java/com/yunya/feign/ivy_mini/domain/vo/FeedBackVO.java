package com.yunya.feign.ivy_mini.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 简介:
 *
 * @author: ylx
 * @date: 2022/5/16
 * @description:
 */
@Data
@Accessors(chain = true)
@ApiModel(description = "意见反馈VO")
public class FeedBackVO {

    private Integer id;

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id")
    private Integer fanId;

    @ApiModelProperty(value = "提交用户")
    private String fanName;

    /**
     * 意见内容
     */
    @ApiModelProperty(value = "意见内容")
    private String context;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "提交时间")
    private Date crtTime;
}
