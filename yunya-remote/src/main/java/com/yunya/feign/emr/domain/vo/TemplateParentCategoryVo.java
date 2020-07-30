package com.yunya.feign.emr.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author xiangyang
 * @date 2020/7/30
 */
@Setter
@Getter
@ApiModel(value = "病例模板父分类返回模型")
public class TemplateParentCategoryVo {

    @ApiModelProperty(value = "id")
    private Integer id;

    @ApiModelProperty(value = "分类名称")
    private String name;
}
