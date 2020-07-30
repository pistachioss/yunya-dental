package com.yunya.feign.emr.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author bruce
 * @date 2020/7/28
 */
@Setter
@Getter
@ApiModel(value = "添加病历模板分类模型")
public class MedicalTempCategoryModel {
    @ApiModelProperty(value = "分类父Id", required = true)
    @NotNull
    private Integer parentId;

    @ApiModelProperty(value = "分类名称", required = true)
    @Size(max = 25, message = "名称不能超过25个字")
    @NotBlank
    private String name;
}
