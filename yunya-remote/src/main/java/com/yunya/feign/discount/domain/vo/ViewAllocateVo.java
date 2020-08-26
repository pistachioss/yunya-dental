package com.yunya.feign.discount.domain.vo;

import io.swagger.annotations.*;
import lombok.*;

/**
 * @author xiangyang
 * @date 2020/8/25
 */
@Getter
@Setter
@ApiModel(value = "查看配给模型")
public class ViewAllocateVo {

    @ApiModelProperty(value = "号段")
    private String numberSegment;

    @ApiModelProperty(value = "分配对象")
    private String allocateOrgName;
}
