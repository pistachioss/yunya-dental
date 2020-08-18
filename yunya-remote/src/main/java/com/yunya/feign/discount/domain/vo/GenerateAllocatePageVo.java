package com.yunya.feign.discount.domain.vo;

import io.swagger.annotations.*;
import lombok.*;

import java.time.*;

/**
 * @author xiangyang
 * @date 2020/8/17
 */
@Setter
@Getter
@ApiModel(value = "产品生成分配分页模型")
public class GenerateAllocatePageVo {

    @ApiModelProperty(value = "")
    private Integer couponId;

    @ApiModelProperty(value = "提交人")
    private String submitterName;

    @ApiModelProperty(value = "提交时间")
    private LocalDateTime submitDate;

    @ApiModelProperty(value = "产品名称")
    private String couponName;

    @ApiModelProperty(value = "产品类型")
    private String couponTypeName;

    @ApiModelProperty(value = "计划配给数量")
    private Integer allocateNum;

    @ApiModelProperty(value = "配给人")
    private String allocateUserName;

    @ApiModelProperty(value = "配给时间")
    private LocalDateTime allocateDate;
}
