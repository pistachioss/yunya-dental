package com.yunya.feign.ivy_mini.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 简介:
 *
 * @author: ylx
 * @date: 2022/6/20
 * @description:
 */
@Data
@Accessors(chain = true)
@ApiModel(description = "客服信息")
public class CustomerVO {
    @ApiModelProperty(value = "客服帐号ID")
    private String openKfid;
    @ApiModelProperty(value = "客服名称")
    private String name;
    @ApiModelProperty(value = "客服头像URL")
    private String avatar;
}
