package com.yunya.feign.ivy_mini.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 简介:
 *
 * @author: ylx
 * @date: 2022/6/16
 * @description:
 */
@Data
@ApiModel(value = "取货人列表")
public class FansPickUpVO {

    @ApiModelProperty(value = "Id")
    private Integer Id;
    /**
     * 微信用户id
     */
    @ApiModelProperty(value = "微信用户id")
    private Integer fansId;
    @ApiModelProperty(value = "取货人名称")
    private String name;
    @ApiModelProperty(value = "取货人电话")
    private String phoneNumber;
    @ApiModelProperty(value = "是否为默认 0-否 1-是")
    private Integer defaultStatus;
}
