package com.yunya.feign.treatment_other.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @description: 上次文件模型
 * @author: chenlin
 * @create: 2021-10-19 15:52
 **/
@ApiModel(description = "上次文件模型")
@Data
@ToString
public class XUploadFileModel implements Serializable {

    /** 文件资源定位路径*/
    @ApiModelProperty(value = "文件资源定位路径",required = true)
    @NotBlank(message = "文件资源定位路径不能为空")
    private String fileLocation;

    /** 文件名称*/
    @ApiModelProperty(value = "文件名称",required = true)
    @NotBlank(message = "文件名称不能为空")
    private String fileName;
}
