package com.yunya.feign.system.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * @program: yunya-dental
 * @description: APP端版本控制器编辑表单
 * @author: LHB
 * @create: 2021-03-09 16:36
 **/
@ApiModel(value = "AppVersionEditForm", description = "APP端版本控制器编辑表单")
@Data
public class AppVersionEditForm extends BaseAppVersionForm implements Serializable {
    /**
     * 主键
     */
    @ApiModelProperty(value = "主键",required = true)
    @NotNull(message = "主键不能为空！")
    private Integer id;
    /**
     * 更新人
     */
    @ApiModelProperty(value = "更新人")
    private String updateName;
}
