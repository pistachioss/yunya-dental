package com.yunya.feign.discount.domain.query;

import com.fasterxml.jackson.annotation.*;
import io.swagger.annotations.*;
import lombok.*;

import javax.validation.constraints.*;
import java.time.*;

/**
 * @author xiangyang
 * @date 2020/8/24
 */
@Getter
@Setter
@ApiModel(value = "查看分配查询模型")
public class GenerateAllocateQuery {

    @ApiModelProperty(value = "优惠券id", required = true)
    @NotNull
    private Integer couponId;
    @ApiModelProperty(value = "提交时间", required = true)
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime submitDate;
}
