package com.yunya.feign.emr.domain.model;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author bruce
 * @date 2020/7/28
 */
@Setter
@Getter
@ApiModel("添加病历模板分类模型")
public class MedicalTempCategoryModel {
    @NotNull
    private Integer parentId;
    @Size(max = 25, message = "名称不能超过25个字")
    @NotBlank
    private String name;
}
