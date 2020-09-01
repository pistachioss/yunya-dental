package com.yunya.feign.emr.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;

/**
 * @author xiangyang
 * @date 2020/7/29
 */
@Setter
@Getter
@ApiModel(value = "模板查询参数模型（词条，范句，要点，诊断，病例）")
public class TemplateQuery {

    @ApiModelProperty(value = "查询条件")
    private String keyword;

    @ApiModelProperty(value = "1：启用 0：禁用")
    private Integer enable;

    @ApiModelProperty(value = "页码", required = true)
    @NotNull
    private Integer pageNum;
    @ApiModelProperty(value = "每页数量", required = true)
    @NotNull
    private Integer pageSize;
}
