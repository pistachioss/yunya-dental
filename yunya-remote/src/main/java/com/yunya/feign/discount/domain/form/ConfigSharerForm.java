package com.yunya.feign.discount.domain.form;

import io.swagger.annotations.*;
import lombok.*;

import javax.validation.constraints.*;

/**
 * @author xiangyang
 * @date 2020/8/30
 */
@Getter
@Setter
@ApiModel(value = "配置共享人模型")
public class ConfigSharerForm {
    @ApiModelProperty(value = "共享人", example = "1,2,3", required = true)
    @NotBlank
    private String sharerIdStr;
}
