package com.yunya.feign.report.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 简介：项目查询模型
 *
 * @author: chenlin
 * @Description:
 * @Date: 2021/3/19 10:11
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("项目查询模型")
public class CategoryQuery implements Serializable {
    /** 项目类型：0-价目；1-商品*/
    @ApiModelProperty(value = "项目类型：0-价目；1-商品", required = true)
    @NotNull(message = "项目类型不能为空")
    private Integer itemType;
    /** 项目分类ID*/
    @ApiModelProperty("项目分类ID")
    private Integer categoryId;
}
