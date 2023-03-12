package com.yunya.feign.emr.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author xiangyang
 * @date 2020/7/29
 */
@Setter
@Getter
@ApiModel("病例模板分类查询模型")
public class TemplateCategoryVo {
    @ApiModelProperty("分类Id")
    private Integer id;
    @ApiModelProperty("排序字段")
    private Integer sort;
    @ApiModelProperty("分类名称")
    private String name;
    @ApiModelProperty(value = "子分类集合")
    private List<TemplateCategoryVo> childList;
}

