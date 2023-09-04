package com.yunya.feign.treatment_other.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author: chenlin
 * @date: 2023/9/4 14:49
 * @description: 项目明细修改模型
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("项目明细修改模型")
public class TariffPackageDetailForm implements Serializable {
    /** 明细id */
    @ApiModelProperty("明细id")
    private Integer id;

    /** 项目数量 */
    @ApiModelProperty(value = "项目数量", required = true)
    @NotNull(message = "项目数量不能为空")
    @Min(value = 1, message = "项目数量不能小于1")
    private Integer quantity;
}
