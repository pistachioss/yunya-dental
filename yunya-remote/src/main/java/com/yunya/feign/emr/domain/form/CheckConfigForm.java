package com.yunya.feign.emr.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 简介：检查设置修改模型
 *
 * @author: chenlin
 * @Description: 检查设置修改模型
 * @Date: 2022/1/7 18:37
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("检查设置修改模型")
public class CheckConfigForm implements Serializable {

    @ApiModelProperty(value = "检查id", required = true)
    @NotNull(message = "检查id不能为空")
    private Integer id;

    @ApiModelProperty(value = "检查名称", required = true)
    @NotEmpty(message = "检查名称不能为空")
    private String checkName;
}
