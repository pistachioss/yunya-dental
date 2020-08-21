package com.yunya.feign.discount.domain.model;

import io.swagger.annotations.*;
import lombok.*;

import javax.validation.constraints.*;
import java.time.*;
import java.util.*;

/**
 * @author xiangyang
 * @date 2020/8/19
 */
@Getter
@Setter
@ApiModel(value = "生成分配卡券模型")
public class GenerateAllocateModel {

    @ApiModelProperty(value = "优惠券id")
    @NotNull
    private Integer couponId;

    @ApiModelProperty(value = "优惠券code")
    @NotBlank
    private String couponCode;

    @ApiModelProperty(value = "提交时间")
    @NotNull
    private LocalDateTime submitDate;

    @ApiModelProperty(value = "配给集合")
    @NotEmpty
    private List<ClinicAllocateModel> allocateList;
}
