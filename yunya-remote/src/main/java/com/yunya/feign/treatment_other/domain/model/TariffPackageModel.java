package com.yunya.feign.treatment_other.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * @author: chenlin
 * @date: 2023/9/4 13:14
 * @description: 项目组合添加模型
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("项目组合添加模型")
public class TariffPackageModel implements Serializable {

    @ApiModelProperty(value = "名称", required = true)
    @NotEmpty(message = "名称不能为空")
    @Max(value = 50, message = "名称长度不能超过50")
    private String name;
}
