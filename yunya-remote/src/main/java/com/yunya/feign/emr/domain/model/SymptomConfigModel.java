package com.yunya.feign.emr.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 简介：症状设置添加模型
 *
 * @author: chenlin
 * @Description: 症状设置添加模型
 * @Date: 2022/1/7 18:33
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("症状设置添加模型")
public class SymptomConfigModel implements Serializable {

    @ApiModelProperty(value = "检查id", required = true)
    @NotNull(message = "检查id不能为空")
    private Integer checkId;

    @ApiModelProperty(value = "症状名称", required = true)
    @NotEmpty(message = "症状名称不能为空")
    private String symptomName;

    @ApiModelProperty(value = "备注")
    private String remark;
}
