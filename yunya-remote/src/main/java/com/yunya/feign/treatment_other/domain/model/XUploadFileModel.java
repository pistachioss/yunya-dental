package com.yunya.feign.treatment_other.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    /** 上传日期*/
    @ApiModelProperty(value = "上传日期,不传默认当前日期")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private String uploadTime;
}
