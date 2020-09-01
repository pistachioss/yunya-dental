package com.yunya.feign.emr.domain.query;

import io.swagger.annotations.*;
import lombok.*;

import javax.validation.constraints.*;

/**
 * @author xiangyang
 * @date 2020/8/7
 */
@Setter
@Getter
@ApiModel(value = "病例变更查询模型")
public class ChangeApproveQuery {

    @ApiModelProperty(value = "查询关键字")
    private String keyword;

    @ApiModelProperty(value = "页码", required = true)
    @NotNull
    private Integer pageNum;
    @ApiModelProperty(value = "每页数量", required = true)
    @NotNull
    private Integer pageSize;
}
