package com.yunya.feign.emr.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 简介:
 *
 * @author: ylx
 * @date: 2023/3/7
 * @description:
 */
@Data
@ApiModel(value = "修改病历模板排序模型")
public class MedicalTempSortForm {
    @ApiModelProperty("分类（0：普通模板 1：病例模板）")
    private Integer type;
    @ApiModelProperty("修改列表")
    private List<MedicalTempCategorySortForm> medicalTempCategorySortForms;
}
