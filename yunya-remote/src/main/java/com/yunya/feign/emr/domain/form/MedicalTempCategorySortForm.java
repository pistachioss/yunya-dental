package com.yunya.feign.emr.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 简介:
 *
 * @author: ylx
 * @date: 2023/3/7
 * @description:
 */
@Setter
@Getter
@ApiModel(value = "修改病历模板分类排序模型")
public class MedicalTempCategorySortForm {
    @ApiModelProperty("ID")
    private Integer id;

    @ApiModelProperty("排序字段")
    private Integer sort;
}
