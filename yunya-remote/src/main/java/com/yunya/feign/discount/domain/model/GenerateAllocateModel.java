package com.yunya.feign.discount.domain.model;

import com.fasterxml.jackson.annotation.*;
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

    @ApiModelProperty(value = "优惠券id", required = true)
    @NotNull
    private Integer couponId;

    @ApiModelProperty(value = "优惠券code", required = true)
    @NotBlank
    private String couponCode;

    @ApiModelProperty(value = "提交时间", required = true)
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime submitDate;

    @ApiModelProperty(value = "配给集合", required = true)
    @NotEmpty
    private List<ClinicAllocateModel> allocateList;
}
