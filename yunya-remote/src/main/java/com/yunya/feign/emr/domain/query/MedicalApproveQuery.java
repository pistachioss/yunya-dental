package com.yunya.feign.emr.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;

/**
 * @author xiangyang
 * @date 2020/8/6
 */
@Setter
@Getter
@ApiModel(value = "病例审核查询模型")
public class MedicalApproveQuery {

    @ApiModelProperty(value = "查询关键字")
    private String keyword;

    @ApiModelProperty(value = "草稿提交时间（需求改动为时间段，字段停用）")
    private String submitTime;
    @ApiModelProperty(value = "草稿提交开始时间")
    private String startTime;
    @ApiModelProperty(value = "草稿提交结束时间")
    private String endTime;

    @ApiModelProperty(value = "页码", required = true)
    @NotNull
    private Integer pageNum;
    @ApiModelProperty(value = "每页数量", required = true)
    @NotNull
    private Integer pageSize;
}
