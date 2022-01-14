package com.yunya.feign.emr.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * 简介：检查设置添加模型
 *
 * @author: chenlin
 * @Description: 检查设置添加模型
 * @Date: 2022/1/7 18:33
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("检查设置添加模型")
public class CheckConfigModel implements Serializable {

    @ApiModelProperty(value = "检查名称", required = true)
    @NotEmpty(message = "检查名称不能为空")
    private String checkName;
}
