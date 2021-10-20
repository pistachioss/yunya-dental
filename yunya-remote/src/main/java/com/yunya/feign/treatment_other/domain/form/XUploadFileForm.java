package com.yunya.feign.treatment_other.domain.form;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @description: 上传文件编辑参数模型
 * @author: chenlin
 * @create: 2021-10-20 10:53
 **/
@ApiModel(description = "上传文件编辑参数模型")
@Data
public class XUploadFileForm implements Serializable {
    /** 文件ID*/
    @ApiModelProperty("文件ID")
    private Integer id;

    @ApiModelProperty(value = "上传时间",required = true)
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    @NotNull(message = "上传时间不能为空")
    private String uploadTime;
}
