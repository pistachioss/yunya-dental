package com.yunya.feign.treatment_other.domain.form;

import com.yunya.feign.treatment_other.domain.model.TariffPackageModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;

/**
 * @author: chenlin
 * @date: 2023/9/4 13:16
 * @description: 项目组合修改模型
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("项目组合修改模型")
public class TariffPackageForm extends TariffPackageModel {

    @ApiModelProperty(value = "id", required = true)
    @NotNull(message = "id不能为空")
    private Integer id;
}
