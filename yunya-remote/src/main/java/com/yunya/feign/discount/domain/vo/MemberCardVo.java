package com.yunya.feign.discount.domain.vo;

import io.swagger.annotations.*;
import lombok.*;

/**
 * @author xiangyang
 * @date 2020/9/3
 */
@Getter
@Setter
@ApiModel(value = "会员卡折扣优惠模型")
public class MemberCardVo {
    @ApiModelProperty(value = "会员卡id")
    private Integer id;
    @ApiModelProperty(value = "卡主")
    private String cardOwner;
    @ApiModelProperty(value = "图片路径")
    private String path;
}
