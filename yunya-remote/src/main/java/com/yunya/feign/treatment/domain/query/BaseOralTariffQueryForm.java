package com.yunya.feign.treatment.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Min;
import java.io.Serializable;

/**
 * 简介: 商品项目列表查询参数模型
 *
 * @author: chow
 * @date: 2020/8/3 20:53
 * @description:
 * @since: 1.0.0
 */
@ApiModel("商品项目列表查询参数模型")
@Data
@ToString
public class BaseOralTariffQueryForm implements Serializable {
    @ApiModelProperty(value = "是否分页,默认true")
    private Boolean whetherPage = true;

    @ApiModelProperty("页码，默认第1页")
    @Min(message = "最小值", value = 1)
    private Integer pageNum = 1;

    @ApiModelProperty("每页显示数量，默认显示10条")
    @Min(message = "最小值", value = 1)
    private Integer pageSize = 10;

    @ApiModelProperty("商品项目ID")
    private Integer id;

    @ApiModelProperty("商品项目分类ID")
    private Integer oralTariffCategoryId;

    @ApiModelProperty("商品项目名称/商品项目编号/拼音缩写")
    private String keyWord;

    @ApiModelProperty("是否启用")
    private Boolean inservice;
}
