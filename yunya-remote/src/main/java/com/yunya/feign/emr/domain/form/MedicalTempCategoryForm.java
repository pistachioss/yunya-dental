package com.yunya.feign.emr.domain.form;

import io.swagger.annotations.ApiModel;
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
@ApiModel("修改病历模板分类模型")
public class MedicalTempCategoryForm {

    @NotNull(message = "请先选中病历模板子分类")
    private Integer id;
    @NotNull
    private Integer parentId;
    @Size(max = 25, message = "名称不能超过25个字")
    @NotBlank
    private String name;
}
