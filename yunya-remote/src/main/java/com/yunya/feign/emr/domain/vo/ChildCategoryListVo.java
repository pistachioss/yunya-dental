package com.yunya.feign.emr.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author xiangyang
 * @date 2020/7/29
 */
@Setter
@Getter
@ApiModel("病例模板子分类查询模型")
public class ChildCategoryListVo {
    @ApiModelProperty("模板子分类Id")
    private Integer id;
    @ApiModelProperty("模板子分类名称")
    private String name;
}

