package com.yunya.feign.discount.domain.vo;

import io.swagger.annotations.*;
import lombok.*;

/**
 * @author xiangyang
 * @date 2020/8/31
 */
@Getter
@Setter
@ApiModel(value = "查看生成分配明细模型")
public class GenerateAllocateDetailVo {
    @ApiModelProperty(value = "配给对象id")
    private Integer orgId;
    @ApiModelProperty(value = "配给对象名称")
    private String orgName;
    @ApiModelProperty(value = "优惠券分配id")
    private Integer couponAllocateId;
    @ApiModelProperty(value = "数量")
    private Integer allocateNum;
}
