package com.yunya.feign.emr.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 简介：症状设置修改模型
 *
 * @author: chenlin
 * @Description: 症状设置修改模型
 * @Date: 2022/1/7 18:37
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("症状设置修改模型")
public class SymptomConfigForm implements Serializable {

    @ApiModelProperty(value = "症状id", required = true)
    @NotNull(message = "症状id不能为空")
    private Integer id;

    @ApiModelProperty(value = "症状名称", required = true)
    @NotEmpty(message = "症状名称不能为空")
    private String symptomName;

    @ApiModelProperty(value = "备注")
    private String remark;
}
